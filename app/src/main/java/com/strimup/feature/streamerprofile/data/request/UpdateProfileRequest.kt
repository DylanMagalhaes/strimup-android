package com.strimup.feature.streamerprofile.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("bio")
    val bio: String?,
    @SerialName("daily_status")
    val dailyStatus: String?,
    @SerialName("twitch_url")
    val twitchUrl: String?,
    @SerialName("youtube_url")
    val youtubeUrl: String?,
    @SerialName("tiktok_url")
    val tiktokUrl: String?,
    @SerialName("instagram_url")
    val instagramUrl: String?,
    @SerialName("kick_url")
    val kickUrl: String?,
    @SerialName("personality")
    val personality: String?,
    @SerialName("personality_secondary")
    val personalitySecondary: String?,
    @SerialName("stream_frequency")
    val streamFrequency: String?,
    @SerialName("average_viewers")
    val averageViewers: String?,
    @SerialName("languages")
    val languages: List<String>?,
    @SerialName("tags")
    val tags: List<Int> = emptyList(),
    @SerialName("videos")
    val videos: List<String> = emptyList()
)

@Serializable
data class UpdateAvatarRequest(
    @SerialName("avatar_url")
    val uri: String,
)