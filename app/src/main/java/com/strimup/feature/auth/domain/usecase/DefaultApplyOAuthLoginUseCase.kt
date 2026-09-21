package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import javax.inject.Inject

class DefaultApplyOAuthLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) : ApplyOAuthLoginUseCase {
    override suspend fun invoke(token: String, refreshToken: String?): Result<Unit> {
        return repository.applyOAuthLogin(token, refreshToken)
    }
}
