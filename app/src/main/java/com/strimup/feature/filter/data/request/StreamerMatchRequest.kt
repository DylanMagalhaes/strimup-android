package com.strimup.feature.filter.data.request

import com.strimup.feature.filter.data.response.FilterJsonDto
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


