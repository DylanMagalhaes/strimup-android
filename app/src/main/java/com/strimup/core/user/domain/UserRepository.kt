package com.strimup.core.user.domain

import com.strimup.core.user.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<UserEntity?>

    suspend fun refreshCurrentUser(): Result<UserEntity?>
}