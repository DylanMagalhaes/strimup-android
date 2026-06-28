package com.strimup.feature.auth.domain.entity

import android.util.JsonToken

data class UserEntity(
    val id: String,
    val token: String,
    val userName: String,
    val email: String,
    val role: String,
    val birthDate: String,
    val gender: String
)
