package com.strimup.feature.push.domain.usecase

import com.strimup.feature.push.domain.NotificationPermissionRepository
import javax.inject.Inject

class DefaultMarkNotificationPermissionAskedUseCase @Inject constructor(
    private val repository: NotificationPermissionRepository,
) : MarkNotificationPermissionAskedUseCase {
    override suspend fun invoke() = repository.markPermissionAsked()
}
