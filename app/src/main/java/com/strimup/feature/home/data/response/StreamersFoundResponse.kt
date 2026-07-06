package com.strimup.feature.home.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamersFoundResponse(
    @SerialName("id")
    val id: String,
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
        @SerialName("tags")
        val tags: List<Tag>?
    ) {
        @Serializable
        data class Tag(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
        )
    }
}

