package com.strimup.feature.auth.domain

import com.strimup.feature.auth.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<UserEntity?>

    suspend fun refreshCurrentUser(): Result<UserEntity?>
}