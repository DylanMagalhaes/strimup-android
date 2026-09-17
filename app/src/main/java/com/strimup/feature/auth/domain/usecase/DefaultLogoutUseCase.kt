package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import javax.inject.Inject

class DefaultLogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) : LogoutUseCase {
    override suspend fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
