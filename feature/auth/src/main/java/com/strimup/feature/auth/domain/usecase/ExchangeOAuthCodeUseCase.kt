package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun interface ExchangeOAuthCodeUseCase {
    suspend operator fun invoke(code: String): Result<LoginResultEntity>
}
