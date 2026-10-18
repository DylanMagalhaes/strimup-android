package com.strimup.feature.schedule.data.mapper

import com.strimup.feature.schedule.data.request.CreateScheduleItemRequest
import com.strimup.feature.schedule.data.response.ScheduleResponse
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity

fun ScheduleResponse.toEntity(): ScheduleItemEntity {
    return ScheduleItemEntity(
        id = this.id,
        title = this.title,
        dayOfWeek = this.dayOfWeek,
        startTime = this.startTime
    )
}

fun NewScheduleItemEntity.toCreateScheduleItemRequest(): CreateScheduleItemRequest {
    return CreateScheduleItemRequest(
        dayOfWeek = this.dayOfWeek,
        startTime = this.startTime,
        title = this.title
    )
}
