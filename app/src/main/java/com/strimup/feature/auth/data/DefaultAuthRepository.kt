package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.data.request.LoginRequest
import com.strimup.feature.auth.data.response.UserLoggedResponse
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.UserEntity
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val service: AuthApiService,
    private val authPreferencesDataSource: AuthPreferencesDataSource
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<UserEntity> {
        return runCatching {
            val response = service.login(LoginRequest(email, password))

            authPreferencesDataSource.saveAuthToken(response.token)

            response.toEntity()
        }
    }
}