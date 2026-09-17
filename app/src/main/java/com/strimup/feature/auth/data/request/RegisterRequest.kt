package com.strimup.feature.auth.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("role")
    val role: String,
    @SerialName("pseudo")
    val userName: String,
    @SerialName("password")
    val password: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("email")
    val email: String,
    @SerialName("birth_date")
    val birthDate: String,
)
