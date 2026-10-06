package com.strimup.feature.schedule.domain.entity

data class NewScheduleItemEntity(
    val title: String,
    val dayOfWeek: Int,
    val startTime: String,
)
