package com.strimup.feature.report.domain

import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason

interface ReportRepository {
    suspend fun reportStreamer(streamerId: String, reason: ReportReason, details: String?): Result<ReportOutcome>
}
