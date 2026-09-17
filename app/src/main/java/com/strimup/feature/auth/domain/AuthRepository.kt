package com.strimup.feature.auth.domain

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.RegisterCredentials

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResultEntity>

    suspend fun register(credentials: RegisterCredentials): Result<LoginResultEntity>
}