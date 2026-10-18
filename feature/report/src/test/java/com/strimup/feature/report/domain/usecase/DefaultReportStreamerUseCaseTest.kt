package com.strimup.feature.report.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.entity.InvalidStreamerReportException
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultReportStreamerUseCaseTest {

    private data class SentReport(val streamerId: String, val reason: ReportReason, val details: String?)

    private class FakeReportRepository(
        private val outcome: ReportOutcome = ReportOutcome.Submitted,
    ) : ReportRepository {
        val sentReports = mutableListOf<SentReport>()

        override suspend fun reportStreamer(
            streamerId: String,
            reason: ReportReason,
            details: String?,
        ): Result<ReportOutcome> {
            sentReports += SentReport(streamerId, reason, details)
            return Result.success(outcome)
        }
    }

    @Test
    fun `a valid report should be sent with trimmed details`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportStreamerUseCase(repository)

        val result = useCase("42", ReportReason.Impersonation, "  Se fait passer pour Inox  ")

        assertThat(result.getOrNull()).isEqualTo(ReportOutcome.Submitted)
        assertThat(repository.sentReports)
            .containsExactly(SentReport("42", ReportReason.Impersonation, "Se fait passer pour Inox"))
    }

    @Test
    fun `blank details should be sent as null`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportStreamerUseCase(repository)

        useCase("42", ReportReason.SpamOrScam, "   ")

        assertThat(repository.sentReports.single().details).isNull()
    }

    @Test
    fun `an invalid report should fail without calling the repository`() = runTest {
        val repository = FakeReportRepository()
        val useCase = DefaultReportStreamerUseCase(repository)

        val result = useCase("42", ReportReason.Other, "")

        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidStreamerReportException::class.java)
        assertThat(repository.sentReports).isEmpty()
    }

    @Test
    fun `the repository outcome should be returned as is`() = runTest {
        val useCase = DefaultReportStreamerUseCase(FakeReportRepository(ReportOutcome.AlreadyReported))

        val result = useCase("42", ReportReason.Underage, "")

        assertThat(result.getOrNull()).isEqualTo(ReportOutcome.AlreadyReported)
    }
}
