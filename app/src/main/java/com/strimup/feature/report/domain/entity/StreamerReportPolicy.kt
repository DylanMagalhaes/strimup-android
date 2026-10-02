package com.strimup.feature.report.domain.entity

object StreamerReportPolicy {
    const val MAX_DETAILS_LENGTH = 500

    fun isValid(reason: ReportReason, details: String): Boolean {
        val trimmedDetails = details.trim()
        val hasRequiredDetails = reason != ReportReason.Other || trimmedDetails.isNotEmpty()
        return hasRequiredDetails && trimmedDetails.length <= MAX_DETAILS_LENGTH
    }
}
