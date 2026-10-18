package com.strimup.feature.push.domain.usecase

import com.strimup.feature.push.domain.PushRepository
import javax.inject.Inject

class DefaultDeletePushTokenUseCase @Inject constructor(
    private val repository: PushRepository,
) : DeletePushTokenUseCase {
    override suspend fun invoke(): Result<Unit> = repository.deleteLocalToken()
}
