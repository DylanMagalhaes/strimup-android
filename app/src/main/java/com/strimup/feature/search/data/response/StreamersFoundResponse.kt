package com.strimup.feature.search.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamersResponse(
    @SerialName("id")
    val id: String,
    @SerialName("pseudo")
    val userName: String,
    @SerialName("streamerProfile")
    val streamerProfile: StreamerProfile,

    ) {
    @Serializable
    data class StreamerProfile(
        @SerialName("avatar_url")
        val imageUrl: String?,
    )
}

