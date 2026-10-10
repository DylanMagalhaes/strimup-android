package com.strimup.feature.schedule.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultGetMyScheduleUseCaseTest {

    private class FakeScheduleRepository(
        private val mySchedule: List<ScheduleItemEntity>,
    ) : ScheduleRepository {
        override suspend fun getSchedule(streamerId: String): Result<List<ScheduleItemEntity>> =
            Result.success(emptyList())

        override suspend fun getMySchedule(): Result<List<ScheduleItemEntity>> = Result.success(mySchedule)

        override suspend fun createScheduleItem(item: NewScheduleItemEntity): Result<ScheduleItemEntity> =
            Result.failure(UnsupportedOperationException())

        override suspend fun deleteScheduleItem(id: String): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `should return the connected streamer schedule`() = runTest {
        val mySchedule = listOf(ScheduleItemEntity(id = "1", title = "GTA RP", dayOfWeek = 0, startTime = "20:00"))
        val useCase = DefaultGetMyScheduleUseCase(FakeScheduleRepository(mySchedule))

        val result = useCase()

        assertThat(result.getOrNull()).isEqualTo(mySchedule)
    }
}
