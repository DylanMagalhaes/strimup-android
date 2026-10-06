package com.strimup.feature.schedule.domain.entity

data class ScheduleItemEntity(
    val id: String,
    val title: String,
    val dayOfWeek: Int,
    val startTime: String,
)
