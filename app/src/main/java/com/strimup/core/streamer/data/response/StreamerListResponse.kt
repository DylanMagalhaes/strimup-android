package com.strimup.core.streamer.data.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class StreamerListResponse(
    val items: List<StreamerDto> = emptyList(),
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
)
