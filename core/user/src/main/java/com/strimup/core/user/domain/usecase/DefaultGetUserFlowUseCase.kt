package com.strimup.core.user.domain.usecase

import com.strimup.core.user.domain.UserRepository
import com.strimup.core.user.domain.entity.UserEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultGetUserFlowUseCase @Inject constructor(
    private val repository: UserRepository
) : GetUserFlowUseCase {
    override fun invoke(): Flow<UserEntity?> {
        return repository.getCurrentUser()
    }

}