package com.strimup.feature.home.data.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RandomStreamersResponse(
    @SerialName("items")
    val items: List<StreamerData>?,
    @SerialName("total")
    val total: Int?,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("limit")
    val limit: Int? = null
) {
    @Serializable
    data class StreamerData(
        @SerialName("id")
        val id: String?,
        @SerialName("pseudo")
        val pseudo: String?,
        @SerialName("streamerProfile")
        val streamerProfile: StreamerProfile?
    ) {
        @Serializable
        data class StreamerProfile(
            @SerialName("id")
            val id: String?,
            @SerialName("avatar_url")
            val avatarUrl: String?,
            @SerialName("is_verified")
            val isVerified: Boolean?,
            @SerialName("is_live")
            val isLive: Boolean?,
            @SerialName("live_title")
            val liveTitle: String?,
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
            @SerialName("tags")
            val tags: List<Tag>?
        )

        @Serializable
        data class Tag(
            @SerialName("id")
            val id: Int?,
            @SerialName("name")
            val name: String?
        )
    }
}