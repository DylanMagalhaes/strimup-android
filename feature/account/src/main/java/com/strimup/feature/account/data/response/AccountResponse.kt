package com.strimup.feature.account.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    @SerialName("user")
    val user: AccountData,
) {
    @Serializable
    data class AccountData(
        @SerialName("is_twitch_connected")
        val isTwitchConnected: Boolean,
    )
}
