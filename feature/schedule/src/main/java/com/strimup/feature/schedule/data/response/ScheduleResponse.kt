package com.strimup.feature.schedule.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse(
    @SerialName("id")
    val id: String,

    @SerialName("dayOfWeek")
    val dayOfWeek: Int,

    @SerialName("startTime")
    val startTime: String,

    @SerialName("title")
    val title: String,
)
