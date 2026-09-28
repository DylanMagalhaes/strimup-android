package com.strimup.feature.auth.domain.usecase

fun interface StartTwitchLoginUseCase {
    suspend operator fun invoke(): Result<String>
}
