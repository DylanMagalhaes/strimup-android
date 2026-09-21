package com.strimup.feature.auth.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthCompleteResponse(
    @SerialName("message")
    val message: String,

    @SerialName("token")
    val token: String,

    @SerialName("isNewUser")
    val isNewUser: Boolean? = null,

    @SerialName("user")
    val user: UserRegisteredResponse.UserRegistered
)
