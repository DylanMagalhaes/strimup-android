package com.strimup.feature.favorite.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteStreamerResponse(
    @SerialName("id") val id: String,
    @SerialName("pseudo") val pseudo: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_live") val isLive: Boolean = false,
    @SerialName("live_title") val liveTitle: String? = null
)
