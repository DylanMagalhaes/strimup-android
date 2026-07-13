package com.strimup.feature.auth.domain

import com.strimup.feature.auth.domain.entity.LoginResultEntity

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResultEntity>
}