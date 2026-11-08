package com.strimup.feature.report.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.report.domain.entity.InvalidVideoReportException
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason
import com.strimup.feature.report.fake.FakeReportRepository
import com.strimup.feature.report.fake.FakeReportRepository.SentVideoReport
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultReportVideoUseCaseTest {

    @Test
    fun `a valid report should be sent with trimmed details`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportVideoUseCase(repository)

        val result = useCase("video-1", VideoReportReason.Copyright, "  Clip volé à Inox  ")

        assertThat(result.getOrNull()).isEqualTo(VideoReportOutcome.Submitted)
        assertThat(repository.sentVideoReports)
            .containsExactly(SentVideoReport("video-1", VideoReportReason.Copyright, "Clip volé à Inox"))
    }

    @Test
    fun `blank details should be sent as null`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportVideoUseCase(repository)

        useCase("video-1", VideoReportReason.Violence, "   ")

        assertThat(repository.sentVideoReports.single().details).isNull()
    }

    @Test
    fun `an invalid report should fail without calling the repository`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportVideoUseCase(repository)

        val result = useCase("video-1", VideoReportReason.Other, " ")

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidVideoReportException::class.java)
        assertThat(repository.sentVideoReports).isEmpty()
    }

    @Test
    fun `the repository outcome should be returned as is`() = runTest {
        val repository = FakeReportRepository(videoOutcome = VideoReportOutcome.VideoUnavailable)
        val useCase = DefaultReportVideoUseCase(repository)

        val result = useCase("video-1", VideoReportReason.Underage, "")

        assertThat(result.getOrNull()).isEqualTo(VideoReportOutcome.VideoUnavailable)
    }
}
