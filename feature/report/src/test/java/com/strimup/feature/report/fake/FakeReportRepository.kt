package com.strimup.feature.report.fake

import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason

class FakeReportRepository(
    private val streamerOutcome: ReportOutcome = ReportOutcome.Submitted,
    private val videoOutcome: VideoReportOutcome = VideoReportOutcome.Submitted,
) : ReportRepository {
    data class SentStreamerReport(val streamerId: String, val reason: ReportReason, val details: String?)
    data class SentVideoReport(val videoId: String, val reason: VideoReportReason, val details: String?)

    val sentStreamerReports = mutableListOf<SentStreamerReport>()
    val sentVideoReports = mutableListOf<SentVideoReport>()

    override suspend fun reportStreamer(
        streamerId: String,
        reason: ReportReason,
        details: String?,
    ): Result<ReportOutcome> {
        sentStreamerReports += SentStreamerReport(streamerId, reason, details)
        return Result.success(streamerOutcome)
    }

    override suspend fun reportVideo(
        videoId: String,
        reason: VideoReportReason,
        details: String?,
    ): Result<VideoReportOutcome> {
        sentVideoReports += SentVideoReport(videoId, reason, details)
        return Result.success(videoOutcome)
    }
}
