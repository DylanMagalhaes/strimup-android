package com.strimup.feature.auth.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLoggedResponse(
    @SerialName("message")
    val message: String,
    @SerialName("token")
    val token: String,
    @SerialName("user")
    val userLogged: UserLogged
) {
    @Serializable
    data class UserLogged(
        @SerialName("id")
        val id: String,
        @SerialName("email")
        val email: String,
        @SerialName("pseudo")
        val userName: String,
        @SerialName("role")
        val role: String,
        @SerialName("birth_date")
        val birthDate: String,
        @SerialName("gender")
        val gender: String,
    )
}
