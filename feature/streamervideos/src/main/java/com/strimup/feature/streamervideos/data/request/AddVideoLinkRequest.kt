package com.strimup.feature.streamervideos.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AddVideoLinkRequest(
    val url: String,
    val title: String,
    val description: String? = null,
)
