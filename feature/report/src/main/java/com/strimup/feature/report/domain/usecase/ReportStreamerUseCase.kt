package com.strimup.feature.report.domain.usecase

import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason

fun interface ReportStreamerUseCase {
    suspend operator fun invoke(streamerId: String, reason: ReportReason, details: String): Result<ReportOutcome>
}
