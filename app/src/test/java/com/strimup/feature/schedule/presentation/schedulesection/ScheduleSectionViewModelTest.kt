package com.strimup.feature.schedule.presentation.schedulesection

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.usecase.DeleteScheduleItemUseCase
import com.strimup.feature.schedule.domain.usecase.GetScheduleUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleSectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val twoItemsOnMondayAndOneOnFriday = listOf(
        ScheduleItemEntity(id = "1", title = "Just Chatting", dayOfWeek = 0, startTime = "20:00"),
        ScheduleItemEntity(id = "2", title = "GTA RP", dayOfWeek = 0, startTime = "22:00"),
        ScheduleItemEntity(id = "3", title = "Valorant", dayOfWeek = 4, startTime = "21:00"),
    )

    private fun createViewModel(
        getSchedule: GetScheduleUseCase = GetScheduleUseCase { Result.success(emptyList()) },
        deleteScheduleItem: DeleteScheduleItemUseCase = DeleteScheduleItemUseCase { Result.success(Unit) },
    ) = ScheduleSectionViewModel(
        getSchedule = getSchedule,
        deleteScheduleItem = deleteScheduleItem,
    )

    @Test
    fun `initial state should be Loading`() {
        // GIVEN
        val viewModel = createViewModel()

        // THEN
        assertThat(viewModel.state.value).isEqualTo(ScheduleSectionUiState.Loading)
    }

    @Test
    fun `loadSchedule when use case succeeds should group slots by day sorted by day then time`() = runTest {
        // GIVEN
        val items = listOf(
            ScheduleItemEntity(id = "3", title = "Valorant", dayOfWeek = 5, startTime = "21:00"),
            ScheduleItemEntity(id = "2", title = "GTA RP", dayOfWeek = 0, startTime = "22:00"),
            ScheduleItemEntity(id = "1", title = "Just Chatting", dayOfWeek = 0, startTime = "20:00"),
        )
        val viewModel = createViewModel(getSchedule = GetScheduleUseCase { Result.success(items) })

        // WHEN
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // THEN
        val expectedState = ScheduleSectionUiState.Success(
            days = listOf(
                ScheduleDayUi(
                    dayOfWeek = DayOfWeek.MONDAY,
                    slots = listOf(
                        ScheduleSlotUi(id = "1", startTime = "20:00", title = "Just Chatting"),
                        ScheduleSlotUi(id = "2", startTime = "22:00", title = "GTA RP"),
                    ),
                ),
                ScheduleDayUi(
                    dayOfWeek = DayOfWeek.SATURDAY,
                    slots = listOf(ScheduleSlotUi(id = "3", startTime = "21:00", title = "Valorant")),
                ),
            ),
        )
        assertThat(viewModel.state.value).isEqualTo(expectedState)
    }

    @Test
    fun `loadSchedule should map day 0 to monday and day 6 to sunday`() = runTest {
        // GIVEN
        val items = listOf(
            ScheduleItemEntity(id = "1", title = "Live", dayOfWeek = 6, startTime = "20:00"),
            ScheduleItemEntity(id = "2", title = "Live", dayOfWeek = 0, startTime = "20:00"),
        )
        val viewModel = createViewModel(getSchedule = GetScheduleUseCase { Result.success(items) })

        // WHEN
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ScheduleSectionUiState.Success
        assertThat(state.days.map { it.dayOfWeek }).containsExactly(DayOfWeek.MONDAY, DayOfWeek.SUNDAY).inOrder()
    }

    @Test
    fun `loadSchedule should format start time with seconds to hours and minutes`() = runTest {
        // GIVEN
        val items = listOf(ScheduleItemEntity(id = "1", title = "Live", dayOfWeek = 3, startTime = "20:30:00"))
        val viewModel = createViewModel(getSchedule = GetScheduleUseCase { Result.success(items) })

        // WHEN
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ScheduleSectionUiState.Success
        assertThat(state.days.single().slots.single().startTime).isEqualTo("20:30")
    }

    @Test
    fun `loadSchedule should ignore items with an invalid day of week`() = runTest {
        // GIVEN
        val items = listOf(
            ScheduleItemEntity(id = "1", title = "Live", dayOfWeek = 7, startTime = "20:00"),
            ScheduleItemEntity(id = "2", title = "Live", dayOfWeek = 6, startTime = "20:00"),
            ScheduleItemEntity(id = "3", title = "Live", dayOfWeek = -1, startTime = "20:00"),
        )
        val viewModel = createViewModel(getSchedule = GetScheduleUseCase { Result.success(items) })

        // WHEN
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ScheduleSectionUiState.Success
        assertThat(state.days.flatMap { day -> day.slots.map { it.id } }).containsExactly("2")
    }

    @Test
    fun `loadSchedule when use case fails should emit Error state with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            getSchedule = GetScheduleUseCase { Result.failure(DomainException(DomainError.Network)) },
        )

        // WHEN
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // THEN
        val expectedState = ScheduleSectionUiState.Error(messageRes = R.string.error_network)
        assertThat(viewModel.state.value).isEqualTo(expectedState)
    }

    @Test
    fun `onDeleteClick when use case succeeds should remove the slot and keep the other slots of the day`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            getSchedule = GetScheduleUseCase { Result.success(twoItemsOnMondayAndOneOnFriday) },
        )
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // WHEN
        viewModel.onDeleteClick("1")
        advanceUntilIdle()

        // THEN
        val expectedState = ScheduleSectionUiState.Success(
            days = listOf(
                ScheduleDayUi(
                    dayOfWeek = DayOfWeek.MONDAY,
                    slots = listOf(ScheduleSlotUi(id = "2", startTime = "22:00", title = "GTA RP")),
                ),
                ScheduleDayUi(
                    dayOfWeek = DayOfWeek.FRIDAY,
                    slots = listOf(ScheduleSlotUi(id = "3", startTime = "21:00", title = "Valorant")),
                ),
            ),
        )
        assertThat(viewModel.state.value).isEqualTo(expectedState)
    }

    @Test
    fun `onDeleteClick when the last slot of a day is deleted should remove the day`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            getSchedule = GetScheduleUseCase { Result.success(twoItemsOnMondayAndOneOnFriday) },
        )
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // WHEN
        viewModel.onDeleteClick("3")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ScheduleSectionUiState.Success
        assertThat(state.days.map { it.dayOfWeek }).containsExactly(DayOfWeek.MONDAY)
    }

    @Test
    fun `onDeleteClick while the request is running should mark the slot as deleting and ignore a second click`() =
        runTest {
            // GIVEN
            val pendingDelete = CompletableDeferred<Result<Unit>>()
            var deleteCalls = 0
            val viewModel = createViewModel(
                getSchedule = GetScheduleUseCase { Result.success(twoItemsOnMondayAndOneOnFriday) },
                deleteScheduleItem = DeleteScheduleItemUseCase {
                    deleteCalls++
                    pendingDelete.await()
                },
            )
            viewModel.loadSchedule("12")
            advanceUntilIdle()

            // WHEN
            viewModel.onDeleteClick("1")
            viewModel.onDeleteClick("1")
            advanceUntilIdle()

            // THEN
            val state = viewModel.state.value as ScheduleSectionUiState.Success
            assertThat(state.deletingItemIds).containsExactly("1")
            assertThat(deleteCalls).isEqualTo(1)

            pendingDelete.complete(Result.success(Unit))
            advanceUntilIdle()
            assertThat((viewModel.state.value as ScheduleSectionUiState.Success).deletingItemIds).isEmpty()
        }

    @Test
    fun `onDeleteClick when use case fails should keep the slot and emit a snackbar with the mapped error`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            getSchedule = GetScheduleUseCase { Result.success(twoItemsOnMondayAndOneOnFriday) },
            deleteScheduleItem = DeleteScheduleItemUseCase { Result.failure(DomainException(DomainError.Network)) },
        )
        viewModel.loadSchedule("12")
        advanceUntilIdle()
        val stateBeforeDelete = viewModel.state.value

        viewModel.events.test {
            // WHEN
            viewModel.onDeleteClick("1")
            advanceUntilIdle()

            // THEN
            assertThat(awaitItem()).isEqualTo(ScheduleSectionUiEvent.ShowSnackBar(R.string.error_network))
            assertThat(viewModel.state.value).isEqualTo(stateBeforeDelete)
        }
    }
}
