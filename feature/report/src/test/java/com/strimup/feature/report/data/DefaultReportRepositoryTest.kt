package com.strimup.feature.report.data

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.report.data.request.ReportStreamerRequest
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultReportRepositoryTest {

    private class FakeReportApiService(
        private val onReport: () -> Unit = {},
    ) : ReportApiService {
        val requests = mutableListOf<Pair<String, ReportStreamerRequest>>()

        override suspend fun reportStreamer(streamerId: String, request: ReportStreamerRequest) {
            requests += streamerId to request
            onReport()
        }
    }

    private fun httpException(code: Int, message: String? = null): HttpException {
        val body = message?.let { """{ "message": "$it" }""" } ?: ""
        val responseBody = body.toResponseBody("application/json".toMediaTypeOrNull())
        return HttpException(Response.error<Any>(code, responseBody))
    }

    @Test
    fun `reportStreamer should send the API reason and details`() = runTest {
        val service = FakeReportApiService()
        val repository = DefaultReportRepository(service)

        val result = repository.reportStreamer("42", ReportReason.HarassmentOrHate, "Insultes en live")

        assertThat(result.getOrNull()).isEqualTo(ReportOutcome.Submitted)
        assertThat(service.requests).containsExactly(
            "42" to ReportStreamerRequest(reason = "HARASSMENT_OR_HATE", details = "Insultes en live"),
        )
    }

    @Test
    fun `a conflict should mean the profile was already reported`() = runTest {
        val repository = DefaultReportRepository(FakeReportApiService { throw httpException(409) })

        val result = repository.reportStreamer("42", ReportReason.SpamOrScam, null)

        assertThat(result.getOrNull()).isEqualTo(ReportOutcome.AlreadyReported)
    }

    @Test
    fun `a client error should keep the server message`() = runTest {
        val repository = DefaultReportRepository(
            FakeReportApiService { throw httpException(400, "Tu ne peux pas te signaler toi-même") },
        )

        val exception = repository.reportStreamer("42", ReportReason.SpamOrScam, null)
            .exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Server(400, "Tu ne peux pas te signaler toi-même"))
    }

    @Test
    fun `being offline should fail with a Network DomainError`() = runTest {
        val repository = DefaultReportRepository(FakeReportApiService { throw IOException() })

        val exception = repository.reportStreamer("42", ReportReason.SpamOrScam, null)
            .exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Network)
    }
}
