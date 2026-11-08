package com.strimup.feature.report.domain

import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason

interface ReportRepository {
    suspend fun reportStreamer(streamerId: String, reason: ReportReason, details: String?): Result<ReportOutcome>

    suspend fun reportVideo(videoId: String, reason: VideoReportReason, details: String?): Result<VideoReportOutcome>
}
