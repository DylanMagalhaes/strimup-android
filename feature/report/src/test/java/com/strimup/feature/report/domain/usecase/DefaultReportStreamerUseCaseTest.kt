package com.strimup.feature.report.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.report.domain.entity.InvalidStreamerReportException
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.fake.FakeReportRepository
import com.strimup.feature.report.fake.FakeReportRepository.SentStreamerReport
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultReportStreamerUseCaseTest {

    @Test
    fun `a valid report should be sent with trimmed details`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportStreamerUseCase(repository)

        val result = useCase("42", ReportReason.Impersonation, "  Se fait passer pour Inox  ")

        assertThat(result.getOrNull()).isEqualTo(ReportOutcome.Submitted)
        assertThat(repository.sentStreamerReports)
            .containsExactly(SentStreamerReport("42", ReportReason.Impersonation, "Se fait passer pour Inox"))
    }

    @Test
    fun `blank details should be sent as null`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportStreamerUseCase(repository)

        useCase("42", ReportReason.SpamOrScam, "   ")

        assertThat(repository.sentStreamerReports.single().details).isNull()
    }

    @Test
    fun `an invalid report should fail without calling the repository`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportStreamerUseCase(repository)

        val result = useCase("42", ReportReason.Other, "")

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidStreamerReportException::class.java)
        assertThat(repository.sentStreamerReports).isEmpty()
    }

    @Test
    fun `the repository outcome should be returned as is`() = runTest {
        val repository = FakeReportRepository(streamerOutcome = ReportOutcome.AlreadyReported)
        val useCase = DefaultReportStreamerUseCase(repository)

        val result = useCase("42", ReportReason.Underage, "")

        assertThat(result.getOrNull()).isEqualTo(ReportOutcome.AlreadyReported)
    }
}
