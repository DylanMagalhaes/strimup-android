package com.strimup.feature.search.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamersResponse(
    @SerialName("pseudo")
    val userName: String,
    @SerialName("streamerProfile")
    val streamerProfile: StreamerProfile,

    ) {
    @Serializable
    data class StreamerProfile(
        @SerialName("id")
        val id: String,
        @SerialName("avatar_url")
        val imageUrl: String?,
    )
}

