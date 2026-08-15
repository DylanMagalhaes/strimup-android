package com.strimup.feature.filter.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamerMatchResponse(
    @SerialName("items")
    val matchedStreamers: List<MatchedStreamerDto>,

    @SerialName("page")
    val page: Int = 1,

    @SerialName("limit")
    val limit: Int = 20,

    @SerialName("total")
    val total: Int = 0
)

@Serializable
data class MatchedStreamerDto(
    @SerialName("id")
    val id: Int,

    @SerialName("pseudo")
    val username: String,

    @SerialName("avatar_url")
    val imageUrl: String? = null,

    @SerialName("is_verified")
    val isVerified: Boolean = false,

    @SerialName("is_live")
    val isLive: Boolean = false
)