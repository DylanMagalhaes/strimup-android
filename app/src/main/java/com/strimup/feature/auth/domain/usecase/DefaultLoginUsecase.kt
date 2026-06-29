package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.UserEntity
import javax.inject.Inject

class DefaultLoginUsecase @Inject constructor(
    private val repository: AuthRepository
): LoginUsecase {
    override suspend fun invoke(email: String, password: String): Result<UserEntity> {
        return repository.login(email, password)
    }
}