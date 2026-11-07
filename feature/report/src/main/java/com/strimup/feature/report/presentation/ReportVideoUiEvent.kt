package com.strimup.feature.report.presentation

sealed interface ReportVideoUiEvent {
    data object VideoUnavailable : ReportVideoUiEvent

    data object LoginRequired : ReportVideoUiEvent
}
