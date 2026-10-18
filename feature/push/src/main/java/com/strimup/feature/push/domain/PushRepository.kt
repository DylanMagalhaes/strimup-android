package com.strimup.feature.push.domain

interface PushRepository {
    suspend fun registerDevice(token: String? = null): Result<Unit>

    suspend fun unregisterDevice(): Result<Unit>

    suspend fun deleteLocalToken(): Result<Unit>
}
