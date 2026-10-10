package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import javax.inject.Inject

class DefaultMarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : MarkNotificationAsReadUseCase {
    override suspend fun invoke(id: String): Result<Unit> = repository.markAsRead(id)
}
