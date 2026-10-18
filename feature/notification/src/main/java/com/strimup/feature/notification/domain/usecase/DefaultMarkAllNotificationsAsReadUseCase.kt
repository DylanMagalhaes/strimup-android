package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import javax.inject.Inject

class DefaultMarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : MarkAllNotificationsAsReadUseCase {
    override suspend fun invoke(): Result<Unit> = repository.markAllAsRead()
}
