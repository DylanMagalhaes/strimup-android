package com.strimup.feature.schedule.presentation.schedulesection

import androidx.annotation.StringRes

sealed interface ScheduleSectionUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : ScheduleSectionUiEvent
    data object ItemAdded : ScheduleSectionUiEvent
}
