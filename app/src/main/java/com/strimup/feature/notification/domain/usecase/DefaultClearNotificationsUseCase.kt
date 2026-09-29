package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.NotificationRepository
import javax.inject.Inject

class DefaultClearNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository,
) : ClearNotificationsUseCase {
    override fun invoke() = repository.clear()
}
