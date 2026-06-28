package com.strimup.feature.auth.domain

import com.strimup.feature.auth.domain.entity.UserEntity

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<UserEntity>
}