package com.strimup.feature.schedule.presentation.schedulesection

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.ui.text.UiText
import com.strimup.feature.schedule.domain.entity.InvalidScheduleItemException
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import com.strimup.feature.schedule.domain.usecase.CreateScheduleItemUseCase
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
import java.time.LocalTime

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
        createScheduleItem: CreateScheduleItemUseCase = CreateScheduleItemUseCase { item ->
            Result.success(
                ScheduleItemEntity(
                    id = "new",
                    title = item.title,
                    dayOfWeek = item.dayOfWeek,
                    startTime = item.startTime,
                ),
            )
        },
    ) = ScheduleSectionViewModel(
        getSchedule = getSchedule,
        deleteScheduleItem = deleteScheduleItem,
        createScheduleItem = createScheduleItem,
    )

    private fun ScheduleSectionViewModel.fillAddForm(
        day: DayOfWeek = DayOfWeek.MONDAY,
        startTime: LocalTime = LocalTime.of(21, 0),
        title: String = "Nouveau live",
    ) {
        onAddDaySelected(day)
        onAddStartTimeSelected(startTime)
        onAddTitleChange(title)
    }

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

    @Test
    fun `onAddSubmitClick should send the selected day starting at 0 for monday and the time as HH mm`() = runTest {
        // GIVEN
        val sentItems = mutableListOf<NewScheduleItemEntity>()
        val viewModel = createViewModel(
            createScheduleItem = CreateScheduleItemUseCase { item ->
                sentItems += item
                Result.success(ScheduleItemEntity(id = "new", title = item.title, dayOfWeek = 2, startTime = "08:05"))
            },
        )
        viewModel.loadSchedule("12")
        advanceUntilIdle()
        viewModel.fillAddForm(day = DayOfWeek.WEDNESDAY, startTime = LocalTime.of(8, 5), title = "Morning live")

        // WHEN
        viewModel.onAddSubmitClick()
        advanceUntilIdle()

        // THEN
        assertThat(sentItems).containsExactly(
            NewScheduleItemEntity(title = "Morning live", dayOfWeek = 2, startTime = "08:05"),
        )
    }

    @Test
    fun `onAddSubmitClick when use case succeeds should insert the slot in its day and reset the form`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            getSchedule = GetScheduleUseCase { Result.success(twoItemsOnMondayAndOneOnFriday) },
        )
        viewModel.loadSchedule("12")
        advanceUntilIdle()
        viewModel.fillAddForm(day = DayOfWeek.MONDAY, startTime = LocalTime.of(21, 0), title = "Nouveau live")

        viewModel.events.test {
            // WHEN
            viewModel.onAddSubmitClick()
            advanceUntilIdle()

            // THEN
            assertThat(awaitItem()).isEqualTo(ScheduleSectionUiEvent.ItemAdded)
        }
        val state = viewModel.state.value as ScheduleSectionUiState.Success
        assertThat(state.days.first().slots.map { it.id }).containsExactly("1", "new", "2").inOrder()
        assertThat(viewModel.addItemState.value).isEqualTo(AddScheduleItemUiState())
    }

    @Test
    fun `onAddSubmitClick on a day without stream should create the day at the right place`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            getSchedule = GetScheduleUseCase { Result.success(twoItemsOnMondayAndOneOnFriday) },
        )
        viewModel.loadSchedule("12")
        advanceUntilIdle()
        viewModel.fillAddForm(day = DayOfWeek.WEDNESDAY)

        // WHEN
        viewModel.onAddSubmitClick()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ScheduleSectionUiState.Success
        assertThat(state.days.map { it.dayOfWeek })
            .containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
            .inOrder()
    }

    @Test
    fun `onAddSubmitClick when the form is incomplete should not call the use case`() = runTest {
        // GIVEN
        var createCalls = 0
        val viewModel = createViewModel(
            createScheduleItem = CreateScheduleItemUseCase {
                createCalls++
                Result.failure(IllegalStateException())
            },
        )
        viewModel.onAddDaySelected(DayOfWeek.MONDAY)
        viewModel.onAddTitleChange("Sans horaire")

        // WHEN
        viewModel.onAddSubmitClick()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.addItemState.value.isSubmitEnabled).isFalse()
        assertThat(createCalls).isEqualTo(0)
    }

    @Test
    fun `onAddSubmitClick when the item is invalid should show the invalid item message`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            createScheduleItem = CreateScheduleItemUseCase { Result.failure(InvalidScheduleItemException()) },
        )
        viewModel.fillAddForm()

        // WHEN
        viewModel.onAddSubmitClick()
        advanceUntilIdle()

        // THEN
        val addItemState = viewModel.addItemState.value
        assertThat(addItemState.errorMessage).isEqualTo(UiText.Resource(R.string.schedule_add_invalid))
        assertThat(addItemState.isSubmitting).isFalse()
    }

    @Test
    fun `onAddSubmitClick when use case fails should keep the form and show the mapped error`() = runTest {
        // GIVEN
        val viewModel = createViewModel(
            createScheduleItem = CreateScheduleItemUseCase { Result.failure(DomainException(DomainError.Network)) },
        )
        viewModel.fillAddForm(title = "Nouveau live")

        // WHEN
        viewModel.onAddSubmitClick()
        advanceUntilIdle()

        // THEN
        val addItemState = viewModel.addItemState.value
        assertThat(addItemState.errorMessage).isEqualTo(UiText.Resource(R.string.error_network))
        assertThat(addItemState.title).isEqualTo("Nouveau live")
    }

    @Test
    fun `onAddTitleChange should cut the title at the maximum length`() {
        // GIVEN
        val viewModel = createViewModel()

        // WHEN
        viewModel.onAddTitleChange("a".repeat(SchedulePolicy.MAX_TITLE_LENGTH + 10))

        // THEN
        assertThat(viewModel.addItemState.value.title).hasLength(SchedulePolicy.MAX_TITLE_LENGTH)
    }

    @Test
    fun `onAddDismiss should reset the form`() {
        // GIVEN
        val viewModel = createViewModel()
        viewModel.fillAddForm()

        // WHEN
        viewModel.onAddDismiss()

        // THEN
        assertThat(viewModel.addItemState.value).isEqualTo(AddScheduleItemUiState())
    }

    @Test
    fun `canAddItem should be false once the schedule reaches the maximum number of items`() = runTest {
        // GIVEN
        val fullSchedule = List(SchedulePolicy.MAX_ITEMS) { index ->
            ScheduleItemEntity(id = "$index", title = "Live", dayOfWeek = index % 7, startTime = "20:00")
        }
        val viewModel = createViewModel(getSchedule = GetScheduleUseCase { Result.success(fullSchedule) })

        // WHEN
        viewModel.loadSchedule("12")
        advanceUntilIdle()

        // THEN
        assertThat((viewModel.state.value as ScheduleSectionUiState.Success).canAddItem).isFalse()
    }
}
