package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import com.strimup.feature.notification.domain.entity.Notification
import com.strimup.feature.notification.domain.entity.NotificationNotDeletableException
import javax.inject.Inject

class DefaultDeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : DeleteNotificationUseCase {
    override suspend fun invoke(notification: Notification): Result<Unit> {
        if (!notification.isDeletable) return Result.failure(NotificationNotDeletableException())

        return repository.delete(id = notification.id, wasUnread = !notification.isRead)
    }
}
