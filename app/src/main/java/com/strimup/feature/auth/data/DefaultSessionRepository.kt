package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.mapper.toEntity
import com.strimup.feature.auth.domain.SessionRepository
import com.strimup.feature.auth.domain.entity.UserLoggedEntity
import javax.inject.Inject

class DefaultSessionRepository @Inject constructor(
    private val service: AuthApiService,
) : SessionRepository {
    override suspend fun login(email: String, password: String): Result<UserLoggedEntity> {
        return runCatching {
            service.login(email, password).toEntity()
        }
    }
}