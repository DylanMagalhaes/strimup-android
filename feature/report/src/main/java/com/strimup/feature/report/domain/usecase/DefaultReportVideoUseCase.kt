package com.strimup.feature.report.domain.usecase

import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.entity.InvalidVideoReportException
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportPolicy
import com.strimup.feature.report.domain.entity.VideoReportReason
import javax.inject.Inject

class DefaultReportVideoUseCase @Inject constructor(
    private val repository: ReportRepository,
) : ReportVideoUseCase {
    override suspend fun invoke(
        videoId: String,
        reason: VideoReportReason,
        details: String,
    ): Result<VideoReportOutcome> {
        if (!VideoReportPolicy.isValid(reason, details)) {
            return Result.failure(InvalidVideoReportException())
        }

        return repository.reportVideo(
            videoId = videoId,
            reason = reason,
            details = details.trim().takeIf { it.isNotEmpty() },
        )
    }
}
