package com.strimup.feature.schedule.presentation.schedulesection

import androidx.annotation.StringRes
import java.time.DayOfWeek

sealed interface ScheduleSectionUiState {
    data object Loading : ScheduleSectionUiState

    data class Success(
        val days: List<ScheduleDayUi>,
    ) : ScheduleSectionUiState

    data class Error(
        @StringRes val messageRes: Int,
    ) : ScheduleSectionUiState
}

data class ScheduleDayUi(
    val dayOfWeek: DayOfWeek,
    val slots: List<ScheduleSlotUi>,
)

data class ScheduleSlotUi(
    val id: String,
    val startTime: String,
    val title: String,
)
