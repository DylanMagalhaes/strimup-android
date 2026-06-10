package com.strimup.feature.home.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InLiveStreamersResponse(
    @SerialName("items")
    val items: List<StreamerData>?,
    @SerialName("total")
    val total: Int?,
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
            @SerialName("live_title")
            val liveTitle: String?,
            @SerialName("twitch_url")
            val twitchUrl: String?,
            @SerialName("youtube_url")
            val youtubeUrl: String?,
            @SerialName("tiktok_url")
            val tiktokUrl: String?,
            @SerialName("instagram_url")
            val instagramUrl: String?,
            @SerialName("kick_url")
            val kickUrl: String?
        )
    }
}