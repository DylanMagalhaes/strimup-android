package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import javax.inject.Inject

class DefaultExchangeOAuthCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) : ExchangeOAuthCodeUseCase {
    override suspend fun invoke(code: String): Result<LoginResultEntity> {
        return repository.exchangeOAuthCode(code)
    }
}
