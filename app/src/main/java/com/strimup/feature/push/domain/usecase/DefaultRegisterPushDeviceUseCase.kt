package com.strimup.feature.push.domain.usecase

import com.strimup.feature.push.domain.PushRepository
import javax.inject.Inject

class DefaultRegisterPushDeviceUseCase @Inject constructor(
    private val repository: PushRepository,
) : RegisterPushDeviceUseCase {
    override suspend fun invoke(token: String?): Result<Unit> = repository.registerDevice(token)
}
