package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity

fun interface CreateScheduleItemUseCase {
    suspend operator fun invoke(item: NewScheduleItemEntity): Result<ScheduleItemEntity>
}