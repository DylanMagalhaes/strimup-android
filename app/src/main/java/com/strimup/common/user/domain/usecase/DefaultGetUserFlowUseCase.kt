package com.strimup.common.user.domain.usecase

import com.strimup.common.user.domain.UserRepository
import com.strimup.common.user.domain.entity.UserEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultGetUserFlowUseCase @Inject constructor(
    private val repository: UserRepository
): GetUserFlowUseCase {
    override suspend fun invoke(): Flow<UserEntity?> {
        return repository.getCurrentUser()
    }

}