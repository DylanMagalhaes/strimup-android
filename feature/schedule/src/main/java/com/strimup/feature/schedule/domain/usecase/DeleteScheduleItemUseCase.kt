package com.strimup.feature.schedule.domain.usecase

fun interface DeleteScheduleItemUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}
