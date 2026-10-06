package com.strimup.feature.schedule.presentation.schedulesection

import com.strimup.core.ui.text.UiText
import com.strimup.feature.schedule.domain.entity.SchedulePolicy
import java.time.DayOfWeek
import java.time.LocalTime

data class AddScheduleItemUiState(
    val selectedDay: DayOfWeek? = null,
    val startTime: LocalTime? = null,
    val title: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: UiText? = null,
) {
    val isSubmitEnabled: Boolean
        get() = !isSubmitting && selectedDay != null && startTime != null && SchedulePolicy.isValidTitle(title)
}
