package com.strimup.feature.schedule.domain.usecase

import com.strimup.feature.schedule.domain.ScheduleRepository
import javax.inject.Inject

class DefaultDeleteScheduleItemUseCase @Inject constructor(
    private val repository: ScheduleRepository
) : DeleteScheduleItemUseCase {
    override suspend fun invoke(id: String): Result<Unit> = repository.deleteScheduleItem(id)
}
