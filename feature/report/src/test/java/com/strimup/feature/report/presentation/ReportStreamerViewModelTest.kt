package com.strimup.feature.report.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.feature.report.R
import com.strimup.feature.report.domain.entity.InvalidStreamerReportException
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.StreamerReportPolicy
import com.strimup.feature.report.domain.usecase.ReportStreamerUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import com.strimup.core.ui.R as CoreUiR

@OptIn(ExperimentalCoroutinesApi::class)
class ReportStreamerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private data class ReportCall(val streamerId: String, val reason: ReportReason, val details: String)

    private val calls = mutableListOf<ReportCall>()

    private fun buildViewModel(
        result: () -> Result<ReportOutcome> = { Result.success(ReportOutcome.Submitted) },
    ) = ReportStreamerViewModel(
        reportStreamer = ReportStreamerUseCase { streamerId, reason, details ->
            calls += ReportCall(streamerId, reason, details)
            result()
        },
    )

    @Test
    fun `submit should be disabled until a reason is selected`() {
        val viewModel = buildViewModel()

        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()

        viewModel.onReasonSelected(ReportReason.SpamOrScam)

        assertThat(viewModel.state.value.isSubmitEnabled).isTrue()
    }

    @Test
    fun `the other reason should require details before enabling submit`() {
        val viewModel = buildViewModel()

        viewModel.onReasonSelected(ReportReason.Other)

        assertThat(viewModel.state.value.isDetailsRequired).isTrue()
        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()

        viewModel.onDetailsChange("Faux compte")

        assertThat(viewModel.state.value.isSubmitEnabled).isTrue()
    }

    @Test
    fun `details should be capped to the maximum length`() {
        val viewModel = buildViewModel()

        viewModel.onDetailsChange("a".repeat(StreamerReportPolicy.MAX_DETAILS_LENGTH + 20))

        assertThat(viewModel.state.value.details).hasLength(StreamerReportPolicy.MAX_DETAILS_LENGTH)
    }

    @Test
    fun `a successful report should reset the form and confirm it`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onReasonSelected(ReportReason.Impersonation)
        viewModel.onDetailsChange("Se fait passer pour Inox")

        viewModel.events.test {
            viewModel.onSubmitClick("42")

            assertThat(awaitItem()).isEqualTo(ReportStreamerUiEvent.Reported(R.string.report_sent))
        }
        assertThat(calls).containsExactly(ReportCall("42", ReportReason.Impersonation, "Se fait passer pour Inox"))
        assertThat(viewModel.state.value).isEqualTo(ReportStreamerUiState())
    }

    @Test
    fun `an already reported profile should show a dedicated confirmation`() = runTest {
        val viewModel = buildViewModel { Result.success(ReportOutcome.AlreadyReported) }
        viewModel.onReasonSelected(ReportReason.SpamOrScam)

        viewModel.events.test {
            viewModel.onSubmitClick("42")

            assertThat(awaitItem()).isEqualTo(ReportStreamerUiEvent.Reported(R.string.report_already_sent))
        }
    }

    @Test
    fun `a client error should keep the form and show the server message`() = runTest {
        val viewModel = buildViewModel {
            Result.failure(DomainException(DomainError.Server(400, "Tu ne peux pas signaler ton propre profil.")))
        }
        viewModel.onReasonSelected(ReportReason.SpamOrScam)

        viewModel.onSubmitClick("42")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isSubmitting).isFalse()
        assertThat(state.selectedReason).isEqualTo(ReportReason.SpamOrScam)
        assertThat(state.errorMessage).isEqualTo(UiText.Dynamic("Tu ne peux pas signaler ton propre profil."))
    }

    @Test
    fun `a network error should show the generic network message`() = runTest {
        val viewModel = buildViewModel { Result.failure(DomainException(DomainError.Network)) }
        viewModel.onReasonSelected(ReportReason.SpamOrScam)

        viewModel.onSubmitClick("42")
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_network))
    }

    @Test
    fun `an invalid report should ask for details`() = runTest {
        val viewModel = buildViewModel { Result.failure(InvalidStreamerReportException()) }
        viewModel.onReasonSelected(ReportReason.SpamOrScam)

        viewModel.onSubmitClick("42")
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(R.string.report_details_required))
    }

    @Test
    fun `submit without a reason should do nothing`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onSubmitClick("42")
        advanceUntilIdle()

        assertThat(calls).isEmpty()
    }

    @Test
    fun `submit should be ignored while a report is being sent`() = runTest {
        val pending = CompletableDeferred<Result<ReportOutcome>>()
        val viewModel = ReportStreamerViewModel(
            reportStreamer = ReportStreamerUseCase { streamerId, reason, details ->
                calls += ReportCall(streamerId, reason, details)
                pending.await()
            },
        )
        viewModel.onReasonSelected(ReportReason.SpamOrScam)

        viewModel.onSubmitClick("42")
        advanceUntilIdle()
        viewModel.onSubmitClick("42")
        advanceUntilIdle()

        assertThat(viewModel.state.value.isSubmitting).isTrue()
        assertThat(calls).hasSize(1)
        pending.complete(Result.success(ReportOutcome.Submitted))
    }

    @Test
    fun `dismissing the sheet should reset the form`() {
        val viewModel = buildViewModel()
        viewModel.onReasonSelected(ReportReason.Other)
        viewModel.onDetailsChange("Faux compte")

        viewModel.onDismiss()

        assertThat(viewModel.state.value).isEqualTo(ReportStreamerUiState())
    }
}
