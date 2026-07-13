package com.strimup.common.user.domain

import com.strimup.common.user.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<UserEntity?>

    suspend fun refreshCurrentUser(): Result<UserEntity?>
}