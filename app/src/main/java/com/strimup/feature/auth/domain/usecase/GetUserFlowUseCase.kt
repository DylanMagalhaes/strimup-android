package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface GetUserFlowUseCase {
    suspend operator fun invoke(): Flow<UserEntity?>

}