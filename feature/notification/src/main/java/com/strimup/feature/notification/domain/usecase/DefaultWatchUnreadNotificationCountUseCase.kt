package com.strimup.feature.notification.domain.usecase

import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.notification.domain.NotificationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultWatchUnreadNotificationCountUseCase @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val repository: NotificationRepository,
) : WatchUnreadNotificationCountUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<Int> = getUser()
        .map { user -> user?.id }
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) {
                repository.clear()
                flowOf(0)
            } else {
                pollUnreadCount()
            }
        }

    private fun pollUnreadCount(): Flow<Int> = channelFlow {
        launch {
            while (isActive) {
                repository.refreshUnreadCount()
                delay(UNREAD_COUNT_REFRESH_INTERVAL)
            }
        }
        repository.unreadCount.collect { count -> send(count) }
    }
}
