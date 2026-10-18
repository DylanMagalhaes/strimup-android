package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.entity.Notification

fun interface DeleteNotificationUseCase {
    suspend operator fun invoke(notification: Notification): Result<Unit>
}
