package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity

fun interface GetMyScheduleUseCase {
    suspend operator fun invoke(): Result<List<ScheduleItemEntity>>
}
