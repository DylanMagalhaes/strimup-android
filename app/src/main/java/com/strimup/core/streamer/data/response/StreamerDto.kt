package com.strimup.core.streamer.data.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class StreamerDto(
    val id: String,
    val streamerId: String? = null,
    val username: String,
    val avatarUrl: String,
    val isVerified: Boolean = false,
    val isLive: Boolean = false,
    val liveTitle: String? = null,
    val dailyStatus: String? = null,
    val platforms: List<Social> = emptyList(),
    val ugc: Ugc? = null,

    val bio: String? = null,
    val languages: List<String> = emptyList(),
    val personality: String? = null,
    val personalitySecondary: String? = null,
    val streamFrequency: String? = null,
    val averageViewers: String? = null,
    val followersCount: Int? = null,
    val isTwitchConnected: Boolean? = null,
    val videos: List<Video> = emptyList(),
    val tags: List<Tag> = emptyList(),
) {
    val isFullProfile: Boolean get() = followersCount != null

    @Serializable
    data class Social(
        val type: String,
        val url: String,
    )

    @Serializable
    data class Ugc(
        val available: Boolean,
        val hasActiveOffers: Boolean,
    )

    @Serializable
    data class Video(
        val id: String,
        val title: String,
        val url: String,
        val description: String? = null,
        val order: Int,
    )

    @Serializable
    data class Tag(
        val id: Int,
        val name: String,
        val category: String,
    )
}
