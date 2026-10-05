package com.strimup.feature.schedule.presentation.schedulesection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.usecase.DeleteScheduleItemUseCase
import com.strimup.feature.schedule.domain.usecase.GetScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduleSectionViewModel @Inject constructor(
    private val getSchedule: GetScheduleUseCase,
    private val deleteScheduleItem: DeleteScheduleItemUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ScheduleSectionUiState>(ScheduleSectionUiState.Loading)
    val state: StateFlow<ScheduleSectionUiState> = _state.asStateFlow()

    private val _events = Channel<ScheduleSectionUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun loadSchedule(streamerId: String) {
        viewModelScope.launch {
            _state.value = ScheduleSectionUiState.Loading

            getSchedule(streamerId)
                .onSuccess { items ->
                    _state.value = ScheduleSectionUiState.Success(days = items.toScheduleDays())
                }
                .onFailure { exception ->
                    _state.value = ScheduleSectionUiState.Error(
                        messageRes = exception.toDomainError().toMessageRes(),
                    )
                }
        }
    }

    fun onDeleteClick(itemId: String) {
        val currentState = _state.value
        if (currentState !is ScheduleSectionUiState.Success || itemId in currentState.deletingItemIds) return

        updateSuccess { it.copy(deletingItemIds = it.deletingItemIds + itemId) }

        viewModelScope.launch {
            deleteScheduleItem(itemId)
                .onSuccess {
                    updateSuccess {
                        it.copy(
                            days = it.days.withoutSlot(itemId),
                            deletingItemIds = it.deletingItemIds - itemId,
                        )
                    }
                }
                .onFailure { exception ->
                    updateSuccess { it.copy(deletingItemIds = it.deletingItemIds - itemId) }
                    _events.send(ScheduleSectionUiEvent.ShowSnackBar(exception.toDomainError().toMessageRes()))
                }
        }
    }

    private fun updateSuccess(transform: (ScheduleSectionUiState.Success) -> ScheduleSectionUiState.Success) {
        _state.update { state ->
            if (state is ScheduleSectionUiState.Success) transform(state) else state
        }
    }

    private fun List<ScheduleDayUi>.withoutSlot(itemId: String): List<ScheduleDayUi> =
        map { day -> day.copy(slots = day.slots.filterNot { it.id == itemId }) }
            .filter { it.slots.isNotEmpty() }

    private fun List<ScheduleItemEntity>.toScheduleDays(): List<ScheduleDayUi> =
        mapNotNull { item -> item.dayOfWeek.toDayOfWeekOrNull()?.let { day -> day to item.toSlotUi() } }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .toSortedMap()
            .map { (day, slots) ->
                ScheduleDayUi(dayOfWeek = day, slots = slots.sortedBy { it.startTime })
            }

    private fun Int.toDayOfWeekOrNull(): DayOfWeek? = try {
        DayOfWeek.of(this + 1)
    } catch (_: DateTimeException) {
        null
    }

    private fun ScheduleItemEntity.toSlotUi(): ScheduleSlotUi = ScheduleSlotUi(
        id = id,
        startTime = startTime.toDisplayTime(),
        title = title,
    )

    private fun String.toDisplayTime(): String = try {
        LocalTime.parse(this).format(TIME_FORMATTER)
    } catch (_: DateTimeException) {
        this
    }

    private companion object {
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
