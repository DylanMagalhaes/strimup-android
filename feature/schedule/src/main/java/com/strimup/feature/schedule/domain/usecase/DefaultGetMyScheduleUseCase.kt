package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import javax.inject.Inject

class DefaultGetMyScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) : GetMyScheduleUseCase {
    override suspend fun invoke(): Result<List<ScheduleItemEntity>> = repository.getMySchedule()
}
