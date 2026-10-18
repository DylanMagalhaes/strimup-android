package com.strimup.feature.auth.domain.usecase

fun interface LogoutUseCase {
    suspend operator fun invoke(): Result<Unit>
}
