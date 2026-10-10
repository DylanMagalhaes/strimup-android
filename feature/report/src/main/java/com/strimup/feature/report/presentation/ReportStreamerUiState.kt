package com.strimup.feature.report.presentation

import com.strimup.core.ui.text.UiText
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.StreamerReportPolicy

data class ReportStreamerUiState(
    val selectedReason: ReportReason? = null,
    val details: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: UiText? = null,
) {
    val isDetailsRequired: Boolean
        get() = selectedReason == ReportReason.Other

    val isSubmitEnabled: Boolean
        get() = !isSubmitting && selectedReason?.let { StreamerReportPolicy.isValid(it, details) } == true
}
