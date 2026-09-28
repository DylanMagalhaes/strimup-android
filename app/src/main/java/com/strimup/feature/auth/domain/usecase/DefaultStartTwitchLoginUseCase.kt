package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import javax.inject.Inject

class DefaultStartTwitchLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) : StartTwitchLoginUseCase {
    override suspend fun invoke(): Result<String> {
        return repository.createTwitchLoginUrl()
    }
}
