package com.strimup.feature.auth.domain.usecase

fun interface ApplyOAuthLoginUseCase {
    suspend operator fun invoke(token: String, refreshToken: String?): Result<Unit>
}
