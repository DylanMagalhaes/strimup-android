package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity

fun interface GetScheduleUseCase {
    suspend operator fun invoke(streamerId: String): Result<List<ScheduleItemEntity>>
}
