package com.strimup.feature.notification.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class NotificationPageResponse(
    @SerialName("items")
    val items: List<NotificationResponse> = emptyList(),
    @SerialName("total")
    val total: Int = 0,
    @SerialName("hasMore")
    val hasMore: Boolean = false,
)

@Serializable
data class NotificationResponse(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: String,
    @SerialName("message")
    val message: String = "",
    @SerialName("data")
    val data: JsonElement? = null,
    @SerialName("read_at")
    val readAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)

@Serializable
data class UnreadCountResponse(
    @SerialName("count")
    val count: Int,
)
