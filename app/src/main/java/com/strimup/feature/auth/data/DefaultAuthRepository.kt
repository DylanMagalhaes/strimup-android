package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.data.request.LoginRequest
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.UserEntity
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val service: AuthApiService,
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<UserEntity> {
        return runCatching {
            service.login(LoginRequest(email, password)).toEntity()
        }
    }
}