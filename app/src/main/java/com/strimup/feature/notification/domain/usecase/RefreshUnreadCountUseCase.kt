package com.strimup.feature.notification.domain.usecase

fun interface RefreshUnreadCountUseCase {
    suspend operator fun invoke(): Result<Int>
}
