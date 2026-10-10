package com.strimup.feature.push.domain

import kotlinx.coroutines.flow.Flow

interface NotificationPermissionRepository {
    val hasAskedPermission: Flow<Boolean>

    suspend fun markPermissionAsked()
}
