package com.strimup.feature.streamerprofile.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileResponse(
    @SerialName("message") val message: String,
    @SerialName("streamer") val streamer: Streamer
) {
    @Serializable
    data class Streamer(
        @SerialName("id") val id: String,
        @SerialName("user_id") val userId: String,
        @SerialName("bio") val bio: String? = null,
        @SerialName("daily_status") val dailyStatus: String? = null,
        @SerialName("avatar_url") val avatarUrl: String? = null,
        @SerialName("twitch_url") val twitchUrl: String? = null,
        @SerialName("youtube_url") val youtubeUrl: String? = null,
        @SerialName("tiktok_url") val tiktokUrl: String? = null,
        @SerialName("instagram_url") val instagramUrl: String? = null,
        @SerialName("kick_url") val kickUrl: String? = null,
        @SerialName("is_verified") val isVerified: Boolean = false,
        @SerialName("is_live") val isLive: Boolean = false,
        @SerialName("live_title") val liveTitle: String? = null,
        @SerialName("birth_date") val birthDate: String? = null,
        @SerialName("personality") val personality: String? = null,
        @SerialName("personality_secondary") val personalitySecondary: String? = null,
        @SerialName("stream_frequency") val streamFrequency: String? = null,
        @SerialName("average_viewers") val averageViewers: String? = null,
        @SerialName("languages") val languages: List<String>? = emptyList(),
        @SerialName("profile_reminder_sent") val profileReminderSent: Boolean = false,
        @SerialName("createdAt") val createdAt: String? = null,
        @SerialName("updatedAt") val updatedAt: String? = null,
        @SerialName("tags") val tags: List<TagDto> = emptyList(),
        @SerialName("videos") val videos: List<String> = emptyList()
    ) {
        @Serializable
        data class TagDto(
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("category") val category: String,
            @SerialName("streamer_tags") val streamerTags: StreamerTagJoinDto? = null
        )

        @Serializable
        data class StreamerTagJoinDto(
            @SerialName("createdAt") val createdAt: String? = null,
            @SerialName("updatedAt") val updatedAt: String? = null,
            @SerialName("StreamerId") val streamerId: String? = null,
            @SerialName("TagId") val tagId: Int? = null
        )
    }
}

@Serializable
data class UpdateAvatarResponse(
    @SerialName("message")
    val message: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
)