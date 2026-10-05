package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import javax.inject.Inject

class DefaultCreateScheduleItemUseCase @Inject constructor(
    private val repository: ScheduleRepository
) : CreateScheduleItemUseCase {
    override suspend fun invoke(item: NewScheduleItemEntity): Result<ScheduleItemEntity> = repository.createScheduleItem(
        item
    )
}