package com.strimup.feature.report.data

import com.strimup.core.network.toDomainResult
import com.strimup.feature.report.data.request.ReportStreamerRequest
import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import retrofit2.HttpException
import javax.inject.Inject

private const val HTTP_CONFLICT = 409

class DefaultReportRepository @Inject constructor(
    private val service: ReportApiService,
) : ReportRepository {

    override suspend fun reportStreamer(
        streamerId: String,
        reason: ReportReason,
        details: String?,
    ): Result<ReportOutcome> {
        val request = ReportStreamerRequest(reason = reason.apiValue, details = details)

        return runCatching {
            service.reportStreamer(streamerId, request)
            ReportOutcome.Submitted
        }.recoverCatching { throwable ->
            if (throwable.isAlreadyReported()) ReportOutcome.AlreadyReported else throw throwable
        }.toDomainResult()
    }

    private fun Throwable.isAlreadyReported(): Boolean = (this as? HttpException)?.code() == HTTP_CONFLICT
}
