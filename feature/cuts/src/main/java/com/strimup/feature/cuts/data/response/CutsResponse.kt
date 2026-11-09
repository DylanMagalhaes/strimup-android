package com.strimup.feature.cuts.data.response

import com.strimup.core.streamer.data.response.StreamerDto
import kotlinx.serialization.Serializable

@Serializable
data class CutsResponse(
    val items: List<Item> = emptyList(),
    val nextCursor: String? = null,
) {
    @Serializable
    data class Item(
        val video: Video,
        val streamer: Streamer,
    )

    @Serializable
    data class Video(
        val id: String,
        val title: String,
        val url: String,
        val description: String? = null,
        val order: Int = 0,
        val embed: StreamerDto.Embed? = null,
        val thumbnailUrl: String? = null,
        val likesCount: Int = 0,
        val likedByMe: Boolean = false,
    )

    @Serializable
    data class Streamer(
        val id: String,
        val username: String,
        val avatarUrl: String? = null,
        val isVerified: Boolean = false,
        val isLive: Boolean = false,
    )
}
