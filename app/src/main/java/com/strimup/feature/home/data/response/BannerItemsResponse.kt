package com.strimup.feature.home.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BannerItemsResponse(

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("imageUrl")
    val imageUrl: String,

    @SerialName("position")
    val position: Int,

    @SerialName("linkUrl")
    val linkUrl: String
)