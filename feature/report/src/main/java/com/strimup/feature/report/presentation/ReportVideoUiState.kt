package com.strimup.feature.report.presentation

import androidx.annotation.StringRes
import com.strimup.core.ui.text.UiText
import com.strimup.feature.report.domain.entity.VideoReportPolicy
import com.strimup.feature.report.domain.entity.VideoReportReason

data class ReportVideoUiState(
    val selectedReason: VideoReportReason? = null,
    val details: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: UiText? = null,
    @StringRes val confirmationRes: Int? = null,
) {
    val isDetailsRequired: Boolean
        get() = selectedReason == VideoReportReason.Other

    val isDetailsTooLong: Boolean
        get() = details.trim().length > VideoReportPolicy.MAX_DETAILS_LENGTH

    val isSubmitEnabled: Boolean
        get() = !isSubmitting && selectedReason?.let { VideoReportPolicy.isValid(it, details) } == true
}
