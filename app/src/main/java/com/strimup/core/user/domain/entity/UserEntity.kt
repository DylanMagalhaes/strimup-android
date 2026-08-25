package com.strimup.core.user.domain.entity

import retrofit2.http.Url

data class UserEntity(
    val id: String,
    val userName: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String?,
)
