package com.strimup.feature.schedule.presentation.schedulesection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import com.strimup.feature.schedule.domain.usecase.GetScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduleSectionViewModel @Inject constructor(
    private val getSchedule: GetScheduleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ScheduleSectionUiState>(ScheduleSectionUiState.Loading)
    val state: StateFlow<ScheduleSectionUiState> = _state.asStateFlow()

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
