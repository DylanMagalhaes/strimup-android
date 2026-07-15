package com.strimup.common.user.domain.entity

data class UserEntity(
    val id: String,
    val userName: String,
    val email: String,
    val role: UserRole,
)
