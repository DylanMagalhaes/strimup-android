package com.strimup.feature.push.domain.usecase

import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.push.domain.PushRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultRegisterRefreshedPushTokenUseCase @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val repository: PushRepository,
) : RegisterRefreshedPushTokenUseCase {
    override suspend fun invoke(token: String): Result<Unit> {
        if (getUser().first() == null) return Result.success(Unit)

        return repository.registerDevice(token)
    }
}
