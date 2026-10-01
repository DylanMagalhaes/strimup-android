package com.strimup.feature.notification.data

import com.strimup.core.network.toDomainResult
import com.strimup.feature.notification.data.mapper.toDomain
import com.strimup.feature.notification.domain.NotificationRepository
import com.strimup.feature.notification.domain.entity.NotificationPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotificationRepository @Inject constructor(
    private val service: NotificationApiService,
) : NotificationRepository {

    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    override suspend fun refreshUnreadCount(): Result<Int> {
        return runCatching { service.getUnreadCount().count.coerceAtLeast(0) }
            .onSuccess { count -> _unreadCount.value = count }
            .toDomainResult()
    }

    override suspend fun getNotifications(offset: Int, limit: Int): Result<NotificationPage> {
        return runCatching { service.getNotifications(limit = limit, offset = offset).toDomain() }
            .toDomainResult()
    }

    override suspend fun markAsRead(id: String): Result<Unit> {
        decrementUnreadCount()
        return runCatching { service.markAsRead(id) }
            .onFailure { incrementUnreadCount() }
            .toDomainResult()
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        val previousCount = _unreadCount.value
        _unreadCount.value = 0
        return runCatching { service.markAllAsRead() }
            .onFailure { _unreadCount.value = previousCount }
            .toDomainResult()
    }

    override suspend fun delete(id: String, wasUnread: Boolean): Result<Unit> {
        if (wasUnread) decrementUnreadCount()
        return runCatching { service.delete(id) }
            .onFailure { if (wasUnread) incrementUnreadCount() }
            .toDomainResult()
    }

    override fun clear() {
        _unreadCount.value = 0
    }

    private fun decrementUnreadCount() {
        _unreadCount.update { (it - 1).coerceAtLeast(0) }
    }

    private fun incrementUnreadCount() {
        _unreadCount.update { it + 1 }
    }
}
