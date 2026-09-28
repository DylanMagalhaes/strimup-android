package com.strimup.feature.auth.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthCompleteRequest(
    @SerialName("tmp")
    val tmp: String,
    @SerialName("code_verifier")
    val codeVerifier: String,
    @SerialName("role")
    val role: String,
    @SerialName("birth_date")
    val birthDate: String,
    @SerialName("gender")
    val gender: String,
)
