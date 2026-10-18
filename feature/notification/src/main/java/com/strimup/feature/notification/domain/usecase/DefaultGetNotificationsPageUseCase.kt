package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import com.strimup.feature.notification.domain.entity.NotificationPage
import javax.inject.Inject

class DefaultGetNotificationsPageUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : GetNotificationsPageUseCase {
    override suspend fun invoke(offset: Int): Result<NotificationPage> {
        return repository.getNotifications(offset = offset, limit = NOTIFICATIONS_PAGE_SIZE)
    }
}
