package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.InvalidScheduleItemException
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import javax.inject.Inject

class DefaultCreateScheduleItemUseCase @Inject constructor(
    private val repository: ScheduleRepository
) : CreateScheduleItemUseCase {
    override suspend fun invoke(item: NewScheduleItemEntity): Result<ScheduleItemEntity> {
        if (!SchedulePolicy.isValid(item)) {
            return Result.failure(InvalidScheduleItemException())
        }

        return repository.createScheduleItem(item.copy(title = item.title.trim()))
    }
}
