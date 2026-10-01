package com.strimup.feature.push.domain.usecase

import com.strimup.feature.push.domain.PushRepository
import javax.inject.Inject

class DefaultUnregisterPushDeviceUseCase @Inject constructor(
    private val repository: PushRepository,
) : UnregisterPushDeviceUseCase {
    override suspend fun invoke(): Result<Unit> = repository.unregisterDevice()
}
