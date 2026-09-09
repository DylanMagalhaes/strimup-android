package com.strimup.core.user.domain.usecase

import com.strimup.core.user.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

fun interface GetUserFlowUseCase {
    operator fun invoke(): Flow<UserEntity?>

}