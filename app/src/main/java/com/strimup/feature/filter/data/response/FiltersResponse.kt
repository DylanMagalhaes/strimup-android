package com.strimup.feature.filter.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilterResponse(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("filter_json")
    val filterJson: FilterJsonDto,

    @SerialName("user_id")
    val userId: String
)

@Serializable
data class FilterJsonDto(
    @SerialName("ageRange")
    val ageRange: List<Int>? = null,

    @SerialName("category")
    val category: String? = null,

    @SerialName("languages")
    val languages: List<String>? = null,

    @SerialName("platforms")
    val platforms: List<String>? = null,

    @SerialName("personalities")
    val personalities: List<String>? = null,

    @SerialName("subCategories")
    val subCategories: List<String>? = null,

    @SerialName("average_viewers")
    val averageViewers: String? = null,

    @SerialName("stream_frequency")
    val streamFrequency: String? = null
)