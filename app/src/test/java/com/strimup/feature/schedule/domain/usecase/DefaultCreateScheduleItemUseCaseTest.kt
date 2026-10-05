package com.strimup.feature.schedule.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.entity.InvalidScheduleItemException
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultCreateScheduleItemUseCaseTest {

    private class FakeScheduleRepository : ScheduleRepository {
        val createdItems = mutableListOf<NewScheduleItemEntity>()

        override suspend fun getSchedule(streamerId: String): Result<List<ScheduleItemEntity>> =
            Result.success(emptyList())

        override suspend fun createScheduleItem(item: NewScheduleItemEntity): Result<ScheduleItemEntity> {
            createdItems += item
            return Result.success(
                ScheduleItemEntity(
                    id = "1",
                    title = item.title,
                    dayOfWeek = item.dayOfWeek,
                    startTime = item.startTime,
                ),
            )
        }

        override suspend fun deleteScheduleItem(id: String): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `a valid item should be created with a trimmed title`() = runTest {
        val repository = FakeScheduleRepository()
        val useCase = DefaultCreateScheduleItemUseCase(repository)

        val result = useCase(NewScheduleItemEntity(title = "  GTA RP  ", dayOfWeek = 0, startTime = "20:00"))

        assertThat(result.getOrNull()?.title).isEqualTo("GTA RP")
        assertThat(repository.createdItems)
            .containsExactly(NewScheduleItemEntity(title = "GTA RP", dayOfWeek = 0, startTime = "20:00"))
    }

    @Test
    fun `a blank title should fail without calling the repository`() = runTest {
        val repository = FakeScheduleRepository()
        val useCase = DefaultCreateScheduleItemUseCase(repository)

        val result = useCase(NewScheduleItemEntity(title = "   ", dayOfWeek = 0, startTime = "20:00"))

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidScheduleItemException::class.java)
        assertThat(repository.createdItems).isEmpty()
    }

    @Test
    fun `a title shorter than the minimum after trimming should fail`() = runTest {
        val useCase = DefaultCreateScheduleItemUseCase(FakeScheduleRepository())

        val result = useCase(NewScheduleItemEntity(title = " a ", dayOfWeek = 0, startTime = "20:00"))

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidScheduleItemException::class.java)
    }

    @Test
    fun `a title longer than the limit should fail`() = runTest {
        val useCase = DefaultCreateScheduleItemUseCase(FakeScheduleRepository())
        val tooLongTitle = "a".repeat(SchedulePolicy.MAX_TITLE_LENGTH + 1)

        val result = useCase(NewScheduleItemEntity(title = tooLongTitle, dayOfWeek = 0, startTime = "20:00"))

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidScheduleItemException::class.java)
    }

    @Test
    fun `a day outside of monday to sunday should fail`() = runTest {
        val useCase = DefaultCreateScheduleItemUseCase(FakeScheduleRepository())

        val result = useCase(NewScheduleItemEntity(title = "Live", dayOfWeek = 7, startTime = "20:00"))

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidScheduleItemException::class.java)
    }

    @Test
    fun `a blank start time should fail`() = runTest {
        val useCase = DefaultCreateScheduleItemUseCase(FakeScheduleRepository())

        val result = useCase(NewScheduleItemEntity(title = "Live", dayOfWeek = 0, startTime = ""))

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidScheduleItemException::class.java)
    }
}
