package com.strimup.feature.streamerprofile.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamerResponse(
    @SerialName("id")
    val id: String?,
    @SerialName("pseudo")
    val pseudo: String?,
    @SerialName("role")
    val role: String?,
    @SerialName("streamerProfile")
    val streamerProfile: StreamerProfile?,
    @SerialName("oauthAccounts")
    val oauthAccounts: List<OAuthAccount>? = emptyList(),
    @SerialName("is_twitch_connected")
    val isTwitchConnected: Boolean? = false
) {

    @Serializable
    data class StreamerProfile(
        @SerialName("id")
        val id: String?,
        @SerialName("bio")
        val bio: String?,
        @SerialName("daily_status")
        val dailyStatus: String?,
        @SerialName("avatar_url")
        val avatarUrl: String?,
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
        @SerialName("is_verified")
        val isVerified: Boolean?,
        @SerialName("is_live")
        val isLive: Boolean?,
        @SerialName("live_title")
        val liveTitle: String?,
        @SerialName("birth_date")
        val birthDate: String?,
        @SerialName("personality")
        val personality: String?,
        @SerialName("personality_secondary")
        val personalitySecondary: String?,
        @SerialName("stream_frequency")
        val streamFrequency: String?,
        @SerialName("average_viewers")
        val averageViewers: Int?,
        @SerialName("languages")
        val languages: List<String>? = emptyList(),
        @SerialName("profile_reminder_sent")
        val profileReminderSent: Boolean?,
        @SerialName("createdAt")
        val createdAt: String?,
        @SerialName("updatedAt")
        val updatedAt: String?,
        @SerialName("followers_count")
        val followersCount: Int?,
        @SerialName("tags")
        val tags: List<Tag>? = emptyList(),
        @SerialName("videos")
        val videos: List<Video>? = emptyList()
    )

    @Serializable
    data class Tag(
        @SerialName("id")
        val id: Int?,
        @SerialName("name")
        val name: String?,
        @SerialName("category")
        val category: String?
    )

    @Serializable
    data class Video(
        @SerialName("id")
        val id: String?,
        @SerialName("title")
        val title: String?,
        @SerialName("description")
        val description: String?,
        @SerialName("order")
        val order: Int?,
        @SerialName("url")
        val url: String?,
    )

    @Serializable
    data class OAuthAccount(
        @SerialName("id")
        val id: String?,
        @SerialName("provider")
        val provider: String?
    )
}