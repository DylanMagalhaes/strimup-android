package com.strimup.feature.auth.domain

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCredentials
import com.strimup.feature.auth.domain.entity.RegisterCredentials

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResultEntity>

    suspend fun register(credentials: RegisterCredentials): Result<LoginResultEntity>

    suspend fun logout(): Result<Unit>

    suspend fun createTwitchLoginUrl(): Result<String>

    suspend fun exchangeOAuthCode(code: String): Result<LoginResultEntity>

    /** Completes a Twitch onboarding (`mode=onboarding`, `tmp` token). */
    suspend fun completeOAuth(credentials: OAuthCredentials): Result<LoginResultEntity>
}