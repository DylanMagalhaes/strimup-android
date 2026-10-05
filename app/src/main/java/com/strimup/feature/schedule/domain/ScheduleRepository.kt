package com.strimup.feature.schedule.domain

import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity

interface ScheduleRepository {
    suspend fun getSchedule(streamerId: String): Result<List<ScheduleItemEntity>>

    suspend fun createScheduleItem(item: NewScheduleItemEntity): Result<ScheduleItemEntity>

    suspend fun deleteScheduleItem(id: String): Result<Unit>
}