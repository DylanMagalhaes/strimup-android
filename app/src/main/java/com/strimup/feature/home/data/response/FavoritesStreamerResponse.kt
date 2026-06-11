package com.strimup.feature.home.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteStreamerResponse(
    @SerialName("id")
    val streamerId: String?,
)