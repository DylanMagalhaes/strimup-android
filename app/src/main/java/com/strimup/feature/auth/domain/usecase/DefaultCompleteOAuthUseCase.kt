package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCredentials
import javax.inject.Inject

class DefaultCompleteOAuthUseCase @Inject constructor(
    private val repository: AuthRepository
) : CompleteOAuthUseCase {
    override suspend fun invoke(credentials: OAuthCredentials): Result<LoginResultEntity> {
        return repository.completeOAuth(credentials)
    }
}
