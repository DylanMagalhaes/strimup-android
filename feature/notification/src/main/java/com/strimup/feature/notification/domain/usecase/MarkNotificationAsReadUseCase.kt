package com.strimup.feature.notification.domain.usecase

fun interface MarkNotificationAsReadUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}
