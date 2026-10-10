package com.strimup.core.streamer.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamerMatchRequest(
    @SerialName("filter")
    val filter: FilterJsonDto,
    @SerialName("page")
    val page: Int = 1,
    @SerialName("limit")
    val limit: Int = 20
)

@Serializable
data class FilterJsonDto(
    @SerialName("ageRange")
    val ageRange: List<Int>? = null,

    @SerialName("category")
    val category: String? = null,

    @SerialName("subCategories")
    val subCategories: List<String>? = null,

    @SerialName("languages")
    val languages: List<String>? = null,

    @SerialName("platforms")
    val platforms: List<String>? = null,

    @SerialName("personalities")
    val personalities: List<String>? = null,

    @SerialName("tags")
    val tags: List<TagDto>? = null,

    @SerialName("average_viewers")
    val averageViewers: String? = null,

    @SerialName("stream_frequency")
    val streamFrequency: String? = null,

    @SerialName("status")
    val status: String? = null
)

@Serializable
data class TagDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,

    @SerialName("category")
    val category: String? = null
)
