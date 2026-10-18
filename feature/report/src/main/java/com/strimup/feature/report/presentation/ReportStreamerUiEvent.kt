package com.strimup.feature.report.presentation

import androidx.annotation.StringRes

sealed interface ReportStreamerUiEvent {
    data class Reported(@StringRes val messageRes: Int) : ReportStreamerUiEvent
}
