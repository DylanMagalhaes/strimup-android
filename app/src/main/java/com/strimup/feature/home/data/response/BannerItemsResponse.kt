package com.strimup.feature.home.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BannerItemsResponse(

    @SerialName("type")
    val type: String,

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("imageUrl")
    val imageUrl: String? = null,

    @SerialName("position")
    val position: Int,

    @SerialName("linkUrl")
    val linkUrl: String,

    @SerialName("streamer")
    val streamer: Streamer? = null
) {
    @Serializable
    data class Streamer(

        @SerialName("avatarUrl")
        val avatarUrl: String? = null,

        )
}