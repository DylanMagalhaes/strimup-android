package com.strimup.feature.schedule.presentation.schedulesection

import androidx.annotation.StringRes
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import java.time.DayOfWeek

sealed interface ScheduleSectionUiState {
    data object Loading : ScheduleSectionUiState

    data class Success(
        val days: List<ScheduleDayUi>,
        val deletingItemIds: Set<String> = emptySet(),
    ) : ScheduleSectionUiState {
        val itemCount: Int
            get() = days.sumOf { it.slots.size }

        val canAddItem: Boolean
            get() = itemCount < SchedulePolicy.MAX_ITEMS
    }

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
