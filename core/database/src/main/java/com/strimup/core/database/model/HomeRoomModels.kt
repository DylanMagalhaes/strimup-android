package com.strimup.core.database.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeSocialRoomModel(
    val url: String?,
    val type: String,
)

@Serializable
data class HomeTagRoomModel(
    val id: Int,
    val name: String,
    val category: String,
)
