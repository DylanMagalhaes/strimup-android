package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import javax.inject.Inject

class DefaultGetScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) : GetScheduleUseCase {
    override suspend fun invoke(streamerId: String): Result<List<ScheduleItemEntity>> = repository.getSchedule(
        streamerId
    )
}