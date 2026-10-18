package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCredentials

fun interface CompleteOAuthUseCase {
    suspend operator fun invoke(credentials: OAuthCredentials): Result<LoginResultEntity>
}
