package com.strimup.feature.auth.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthExchangeRequest(
    @SerialName("code")
    val code: String,
    @SerialName("code_verifier")
    val codeVerifier: String,
)
