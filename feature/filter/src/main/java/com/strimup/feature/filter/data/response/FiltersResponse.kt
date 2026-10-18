package com.strimup.feature.filter.data.response

import com.strimup.core.streamer.data.request.FilterJsonDto
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
