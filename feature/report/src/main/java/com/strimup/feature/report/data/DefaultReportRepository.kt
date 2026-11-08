package com.strimup.feature.report.data

import com.strimup.core.network.toDomainResult
import com.strimup.feature.report.data.request.ReportRequest
import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason
import retrofit2.HttpException
import javax.inject.Inject

private const val HTTP_NOT_FOUND = 404
private const val HTTP_CONFLICT = 409

class DefaultReportRepository @Inject constructor(
    private val service: ReportApiService,
) : ReportRepository {

    override suspend fun reportStreamer(
        streamerId: String,
        reason: ReportReason,
        details: String?,
    ): Result<ReportOutcome> {
        val request = ReportRequest(reason = reason.apiValue, details = details)

        return runCatching {
            service.reportStreamer(streamerId, request)
            ReportOutcome.Submitted
        }.recoverCatching { throwable ->
            if (throwable.httpCode() == HTTP_CONFLICT) ReportOutcome.AlreadyReported else throw throwable
        }.toDomainResult()
    }

    override suspend fun reportVideo(
        videoId: String,
        reason: VideoReportReason,
        details: String?,
    ): Result<VideoReportOutcome> {
        val request = ReportRequest(reason = reason.apiValue, details = details)

        return runCatching {
            service.reportVideo(videoId, request)
            VideoReportOutcome.Submitted
        }.recoverCatching { throwable ->
            when (throwable.httpCode()) {
                HTTP_CONFLICT -> VideoReportOutcome.AlreadyReported
                HTTP_NOT_FOUND -> VideoReportOutcome.VideoUnavailable
                else -> throw throwable
            }
        }.toDomainResult()
    }

    private fun Throwable.httpCode(): Int? = (this as? HttpException)?.code()
}
