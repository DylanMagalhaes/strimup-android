package com.strimup.feature.schedule.presentation.schedulesection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.feature.schedule.R
import com.strimup.feature.schedule.domain.entity.InvalidScheduleItemException
import com.strimup.feature.schedule.domain.entity.NewScheduleItemEntity
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import com.strimup.feature.schedule.domain.usecase.CreateScheduleItemUseCase
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
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleSectionViewModel @Inject constructor(
    private val getSchedule: GetScheduleUseCase,
    private val deleteScheduleItem: DeleteScheduleItemUseCase,
    private val createScheduleItem: CreateScheduleItemUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ScheduleSectionUiState>(ScheduleSectionUiState.Loading)
    val state: StateFlow<ScheduleSectionUiState> = _state.asStateFlow()

    private val _addItemState = MutableStateFlow(AddScheduleItemUiState())
    val addItemState: StateFlow<AddScheduleItemUiState> = _addItemState.asStateFlow()

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

    fun onAddDaySelected(day: DayOfWeek) {
        _addItemState.update { it.copy(selectedDay = day, errorMessage = null) }
    }

    fun onAddStartTimeSelected(startTime: LocalTime) {
        _addItemState.update { it.copy(startTime = startTime, errorMessage = null) }
    }

    fun onAddTitleChange(title: String) {
        _addItemState.update { it.copy(title = title.take(SchedulePolicy.MAX_TITLE_LENGTH), errorMessage = null) }
    }

    fun onAddSubmitClick() {
        val currentState = _addItemState.value
        val day = currentState.selectedDay
        val startTime = currentState.startTime
        if (!currentState.isSubmitEnabled || day == null || startTime == null) return

        _addItemState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            val newItem = NewScheduleItemEntity(
                title = currentState.title,
                dayOfWeek = day.toDayIndex(),
                startTime = startTime.toDisplayTime(),
            )

            createScheduleItem(newItem)
                .onSuccess { createdItem ->
                    _addItemState.value = AddScheduleItemUiState()
                    updateSuccess { it.copy(days = it.days.withSlot(day, createdItem.toSlotUi())) }
                    _events.send(ScheduleSectionUiEvent.ItemAdded)
                }
                .onFailure { exception ->
                    _addItemState.update {
                        it.copy(isSubmitting = false, errorMessage = exception.toAddItemErrorUiText())
                    }
                }
        }
    }

    fun onAddDismiss() {
        if (!_addItemState.value.isSubmitting) {
            _addItemState.value = AddScheduleItemUiState()
        }
    }

    private fun Throwable.toAddItemErrorUiText(): UiText = when (this) {
        is InvalidScheduleItemException -> UiText.Resource(R.string.schedule_add_invalid)
        else -> toDomainError().toUiText()
    }

    private fun updateSuccess(transform: (ScheduleSectionUiState.Success) -> ScheduleSectionUiState.Success) {
        _state.update { state ->
            if (state is ScheduleSectionUiState.Success) transform(state) else state
        }
    }
}
