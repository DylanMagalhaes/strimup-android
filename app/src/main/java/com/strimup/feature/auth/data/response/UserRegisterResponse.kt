package com.strimup.feature.auth.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterResponse(

    @SerialName("token")
    val token: String,

    @SerialName("user")
    val userRegistered: UserRegistered
) {
    @Serializable
    data class UserRegistered(
        @SerialName("id")
        val id: String,
        @SerialName("email")
        val email: String,
        @SerialName("pseudo")
        val userName: String,
        @SerialName("role")
        val role: String,
        @SerialName("birth_date")
        val birthDate: String? = null,
        @SerialName("gender")
        val gender: String? = null,
    )
}
