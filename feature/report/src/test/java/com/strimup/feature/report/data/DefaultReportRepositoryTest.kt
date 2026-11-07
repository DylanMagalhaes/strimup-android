package com.strimup.feature.report.data

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.report.data.request.ReportRequest
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason
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
        val requests = mutableListOf<Pair<String, ReportRequest>>()

        override suspend fun reportStreamer(streamerId: String, request: ReportRequest) {
            requests += streamerId to request
            onReport()
        }

        override suspend fun reportVideo(videoId: String, request: ReportRequest) {
            requests += videoId to request
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
            "42" to ReportRequest(reason = "HARASSMENT_OR_HATE", details = "Insultes en live"),
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

    @Test
    fun `reportVideo should send the API reason and omit missing details`() = runTest {
        val service = FakeReportApiService()
        val repository = DefaultReportRepository(service)

        val result = repository.reportVideo("video-1", VideoReportReason.Violence, null)

        assertThat(result.getOrNull()).isEqualTo(VideoReportOutcome.Submitted)
        assertThat(service.requests).containsExactly("video-1" to ReportRequest(reason = "VIOLENCE"))
    }

    @Test
    fun `a conflict should mean the video was already reported`() = runTest {
        val repository = DefaultReportRepository(FakeReportApiService { throw httpException(409) })

        val result = repository.reportVideo("video-1", VideoReportReason.Copyright, null)

        assertThat(result.getOrNull()).isEqualTo(VideoReportOutcome.AlreadyReported)
    }

    @Test
    fun `a missing video should be reported as unavailable`() = runTest {
        val repository = DefaultReportRepository(
            FakeReportApiService { throw httpException(404, "Vidéo introuvable") },
        )

        val result = repository.reportVideo("video-1", VideoReportReason.Copyright, null)

        assertThat(result.getOrNull()).isEqualTo(VideoReportOutcome.VideoUnavailable)
    }

    @Test
    fun `a video client error should keep the server message`() = runTest {
        val repository = DefaultReportRepository(
            FakeReportApiService { throw httpException(400, "Tu ne peux pas signaler ta propre vidéo.") },
        )

        val exception = repository.reportVideo("video-1", VideoReportReason.SpamOrScam, null)
            .exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Server(400, "Tu ne peux pas signaler ta propre vidéo."))
    }
}
