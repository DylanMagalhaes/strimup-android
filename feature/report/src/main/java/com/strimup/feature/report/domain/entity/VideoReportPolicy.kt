package com.strimup.feature.report.domain.entity

object VideoReportPolicy {
    const val MAX_DETAILS_LENGTH = 500

    fun isValid(reason: VideoReportReason, details: String): Boolean {
        val trimmedDetails = details.trim()
        val hasRequiredDetails = reason != VideoReportReason.Other || trimmedDetails.isNotEmpty()
        return hasRequiredDetails && trimmedDetails.length <= MAX_DETAILS_LENGTH
    }
}
