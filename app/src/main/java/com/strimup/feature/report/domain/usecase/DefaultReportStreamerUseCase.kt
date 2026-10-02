package com.strimup.feature.report.domain.usecase

import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.entity.InvalidStreamerReportException
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.StreamerReportPolicy
import javax.inject.Inject

class DefaultReportStreamerUseCase @Inject constructor(
    private val repository: ReportRepository,
) : ReportStreamerUseCase {
    override suspend fun invoke(streamerId: String, reason: ReportReason, details: String): Result<ReportOutcome> {
        if (!StreamerReportPolicy.isValid(reason, details)) {
            return Result.failure(InvalidStreamerReportException())
        }

        return repository.reportStreamer(
            streamerId = streamerId,
            reason = reason,
            details = details.trim().takeIf { it.isNotEmpty() },
        )
    }
}
