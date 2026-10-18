package com.strimup.feature.schedule.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateScheduleItemRequest(
    @SerialName("dayOfWeek")
    val dayOfWeek: Int,

    @SerialName("startTime")
    val startTime: String,

    @SerialName("title")
    val title: String,
)
