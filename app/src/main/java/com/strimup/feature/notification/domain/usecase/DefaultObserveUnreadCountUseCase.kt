package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DefaultObserveUnreadCountUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : ObserveUnreadCountUseCase {
    override fun invoke(): StateFlow<Int> = repository.unreadCount
}
