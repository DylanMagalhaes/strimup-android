package com.strimup.feature.auth.domain.entity

data class UserEntity(
    val id: String,
    val userName: String,
    val email: String,
    val role: String,
    val birthDate: String,
    val gender: String
)
