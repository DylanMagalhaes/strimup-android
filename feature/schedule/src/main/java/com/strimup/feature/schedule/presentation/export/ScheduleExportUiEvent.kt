package com.strimup.feature.schedule.presentation.export

import androidx.annotation.StringRes

sealed interface ScheduleExportUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : ScheduleExportUiEvent
}
