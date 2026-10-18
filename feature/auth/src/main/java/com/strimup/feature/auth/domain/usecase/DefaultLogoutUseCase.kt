package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.push.domain.usecase.UnregisterPushDeviceUseCase
import javax.inject.Inject

class DefaultLogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val unregisterPushDevice: UnregisterPushDeviceUseCase,
) : LogoutUseCase {
    override suspend fun invoke(): Result<Unit> {
        unregisterPushDevice()
        return repository.logout()
    }
}
