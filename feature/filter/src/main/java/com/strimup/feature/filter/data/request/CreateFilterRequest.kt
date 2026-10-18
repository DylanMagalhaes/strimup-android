package com.strimup.feature.filter.data.request

import com.strimup.core.streamer.data.request.FilterJsonDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateFilterRequest(
    @SerialName("name")
    val name: String,

    @SerialName("filter")
    val filterJson: FilterJsonDto
)
