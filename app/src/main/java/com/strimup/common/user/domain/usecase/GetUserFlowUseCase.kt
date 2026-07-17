package com.strimup.common.user.domain.usecase

import com.strimup.common.user.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface GetUserFlowUseCase {
     operator fun invoke(): Flow<UserEntity?>

}