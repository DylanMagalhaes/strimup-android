package com.strimup.feature.account.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    @SerialName("confirmation")
    val confirmation: String,
    @SerialName("password")
    val password: String? = null,
)
