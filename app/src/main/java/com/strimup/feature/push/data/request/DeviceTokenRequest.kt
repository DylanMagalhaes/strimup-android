package com.strimup.feature.push.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequest(
    @SerialName("token")
    val token: String,
    @SerialName("platform")
    val platform: String,
) {
    companion object {
        private const val ANDROID_PLATFORM = "android"

        fun forAndroid(token: String) = DeviceTokenRequest(token = token, platform = ANDROID_PLATFORM)
    }
}
