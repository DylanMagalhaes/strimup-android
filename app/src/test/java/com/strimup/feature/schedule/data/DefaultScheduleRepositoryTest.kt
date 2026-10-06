package com.strimup.feature.schedule.data

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.schedule.data.request.CreateScheduleItemRequest
import com.strimup.feature.schedule.data.response.ScheduleResponse
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class DefaultScheduleRepositoryTest {

    private class FakeScheduleApiService(
        private val mySchedule: () -> List<ScheduleResponse> = { emptyList() },
    ) : ScheduleApiService {
        override suspend fun getSchedule(streamerId: String): List<ScheduleResponse> = emptyList()

        override suspend fun getMySchedule(): List<ScheduleResponse> = mySchedule()

        override suspend fun deleteScheduleItem(id: String) = Unit

        override suspend fun createScheduleItem(request: CreateScheduleItemRequest): ScheduleResponse =
            ScheduleResponse(
                id = "1",
                dayOfWeek = request.dayOfWeek,
                startTime = request.startTime,
                title = request.title,
            )
    }

    @Test
    fun `getMySchedule should map the connected streamer slots to entities`() = runTest {
        val repository = DefaultScheduleRepository(
            FakeScheduleApiService(
                mySchedule = {
                    listOf(ScheduleResponse(id = "1", dayOfWeek = 0, startTime = "20:00", title = "GTA RP"))
                },
            ),
        )

        val result = repository.getMySchedule()

        assertThat(result.getOrNull()).containsExactly(
            ScheduleItemEntity(id = "1", title = "GTA RP", dayOfWeek = 0, startTime = "20:00"),
        )
    }

    @Test
    fun `getMySchedule should map a network failure to a DomainError`() = runTest {
        val repository = DefaultScheduleRepository(FakeScheduleApiService(mySchedule = { throw IOException() }))

        val result = repository.getMySchedule()

        assertThat((result.exceptionOrNull() as DomainException).error).isEqualTo(DomainError.Network)
    }
}
