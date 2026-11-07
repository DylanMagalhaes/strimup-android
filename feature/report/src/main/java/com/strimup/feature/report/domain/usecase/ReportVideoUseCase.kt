package com.strimup.feature.report.domain.usecase

import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason

fun interface ReportVideoUseCase {
    suspend operator fun invoke(videoId: String, reason: VideoReportReason, details: String): Result<VideoReportOutcome>
}
