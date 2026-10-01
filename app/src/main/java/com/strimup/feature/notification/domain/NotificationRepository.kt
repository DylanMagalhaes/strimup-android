package com.strimup.feature.notification.domain

import com.strimup.feature.notification.domain.entity.NotificationPage
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    val unreadCount: StateFlow<Int>

    suspend fun refreshUnreadCount(): Result<Int>

    suspend fun getNotifications(offset: Int, limit: Int): Result<NotificationPage>

    suspend fun markAsRead(id: String): Result<Unit>

    suspend fun markAllAsRead(): Result<Unit>

    suspend fun delete(id: String, wasUnread: Boolean): Result<Unit>

    fun clear()
}
