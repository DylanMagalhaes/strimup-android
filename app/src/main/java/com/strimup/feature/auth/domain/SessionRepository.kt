package com.strimup.feature.auth.domain

import com.strimup.feature.auth.domain.entity.UserLoggedEntity

interface SessionRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<UserLoggedEntity>
}