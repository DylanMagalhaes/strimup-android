package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import javax.inject.Inject

class DefaultRefreshUnreadCountUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : RefreshUnreadCountUseCase {
    override suspend fun invoke(): Result<Int> = repository.refreshUnreadCount()
}
