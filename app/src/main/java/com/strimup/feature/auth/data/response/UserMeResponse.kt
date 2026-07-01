package com.strimup.feature.auth.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMeResponse(
    @SerialName("user")
    val user: UserMeData
) {
    @Serializable
    data class UserMeData(
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
        @SerialName("is_twitch_connected")
        val isTwitchConnected: Boolean
    )
}
