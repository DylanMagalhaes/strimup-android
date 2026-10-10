package com.strimup.feature.notification.domain.usecase

fun interface MarkAllNotificationsAsReadUseCase {
    suspend operator fun invoke(): Result<Unit>
}
