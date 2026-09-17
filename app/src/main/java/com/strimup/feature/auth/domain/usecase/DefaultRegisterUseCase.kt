package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import javax.inject.Inject

class DefaultRegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) : RegisterUseCase {
    override suspend fun invoke(credentials: RegisterCredentials): Result<LoginResultEntity> {
        return repository.register(credentials)
    }
}
