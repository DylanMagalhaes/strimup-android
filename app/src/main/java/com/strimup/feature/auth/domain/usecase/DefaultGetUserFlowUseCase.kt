package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.UserRepository
import com.strimup.feature.auth.domain.entity.UserEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultGetUserFlowUseCase @Inject constructor(
    private val repository: UserRepository
): GetUserFlowUseCase {
    override suspend fun invoke(): Flow<UserEntity?> {
        return repository.getCurrentUser()
    }

}