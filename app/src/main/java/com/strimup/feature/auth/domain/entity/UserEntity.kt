package com.strimup.feature.auth.domain.entity

import android.util.JsonToken

data class UserEntity(
    val id: String,
    val userName: String,
    val email: String,
    val role: UserRole,
)
