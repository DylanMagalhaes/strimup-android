package com.strimup.feature.report.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.feature.report.R
import com.strimup.feature.report.domain.entity.InvalidVideoReportException
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportPolicy
import com.strimup.feature.report.domain.entity.VideoReportReason
import com.strimup.feature.report.domain.usecase.ReportVideoUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import com.strimup.core.ui.R as CoreUiR

@OptIn(ExperimentalCoroutinesApi::class)
class ReportVideoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private data class ReportCall(val videoId: String, val reason: VideoReportReason, val details: String)

    private val calls = mutableListOf<ReportCall>()

    private fun buildViewModel(
        result: suspend () -> Result<VideoReportOutcome> = { Result.success(VideoReportOutcome.Submitted) },
    ) = ReportVideoViewModel(
        reportVideo = ReportVideoUseCase { videoId, reason, details ->
            calls += ReportCall(videoId, reason, details)
            result()
        },
    )

    @Test
    fun `submit should be disabled until a reason is selected`() {
        val viewModel = buildViewModel()

        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()

        viewModel.onReasonSelected(VideoReportReason.Violence)

        assertThat(viewModel.state.value.isSubmitEnabled).isTrue()
    }

    @Test
    fun `the other reason should require details before enabling submit`() {
        val viewModel = buildViewModel()

        viewModel.onReasonSelected(VideoReportReason.Other)

        assertThat(viewModel.state.value.isDetailsRequired).isTrue()
        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()

        viewModel.onDetailsChange("Vidéo trompeuse")

        assertThat(viewModel.state.value.isSubmitEnabled).isTrue()
    }

    @Test
    fun `details over the limit should be flagged and block submit`() {
        val viewModel = buildViewModel()
        viewModel.onReasonSelected(VideoReportReason.Copyright)

        viewModel.onDetailsChange("a".repeat(VideoReportPolicy.MAX_DETAILS_LENGTH + 1))

        assertThat(viewModel.state.value.isDetailsTooLong).isTrue()
        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()
    }

    @Test
    fun `a successful report should show the confirmation`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onReasonSelected(VideoReportReason.Copyright)
        viewModel.onDetailsChange("Clip volé")

        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        assertThat(calls).containsExactly(ReportCall("video-1", VideoReportReason.Copyright, "Clip volé"))
        assertThat(viewModel.state.value).isEqualTo(ReportVideoUiState(confirmationRes = R.string.report_video_sent))
    }

    @Test
    fun `an already reported video should show a success confirmation`() = runTest {
        val viewModel = buildViewModel { Result.success(VideoReportOutcome.AlreadyReported) }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.confirmationRes).isEqualTo(R.string.report_video_already_sent)
    }

    @Test
    fun `an unavailable video should close the form`() = runTest {
        val viewModel = buildViewModel { Result.success(VideoReportOutcome.VideoUnavailable) }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.events.test {
            viewModel.onSubmitClick("video-1")

            assertThat(awaitItem()).isEqualTo(ReportVideoUiEvent.VideoUnavailable)
        }
        assertThat(viewModel.state.value).isEqualTo(ReportVideoUiState())
    }

    @Test
    fun `an expired session should ask to log in`() = runTest {
        val viewModel = buildViewModel { Result.failure(DomainException(DomainError.Unauthorized)) }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.events.test {
            viewModel.onSubmitClick("video-1")

            assertThat(awaitItem()).isEqualTo(ReportVideoUiEvent.LoginRequired)
        }
        assertThat(viewModel.state.value).isEqualTo(ReportVideoUiState())
    }

    @Test
    fun `a client error should keep the form and show the server message`() = runTest {
        val viewModel = buildViewModel {
            Result.failure(DomainException(DomainError.Server(400, "Tu ne peux pas signaler ta propre vidéo.")))
        }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isSubmitting).isFalse()
        assertThat(state.selectedReason).isEqualTo(VideoReportReason.SpamOrScam)
        assertThat(state.errorMessage).isEqualTo(UiText.Dynamic("Tu ne peux pas signaler ta propre vidéo."))
    }

    @Test
    fun `a network error should show the generic network message`() = runTest {
        val viewModel = buildViewModel { Result.failure(DomainException(DomainError.Network)) }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_network))
    }

    @Test
    fun `an invalid report should ask for details`() = runTest {
        val viewModel = buildViewModel { Result.failure(InvalidVideoReportException()) }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(R.string.report_details_required))
    }

    @Test
    fun `submit should be ignored while a report is being sent`() = runTest {
        val pending = CompletableDeferred<Result<VideoReportOutcome>>()
        val viewModel = buildViewModel { pending.await() }
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)

        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()
        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.isSubmitting).isTrue()
        assertThat(calls).hasSize(1)
        pending.complete(Result.success(VideoReportOutcome.Submitted))
    }

    @Test
    fun `dismissing should reset the form and the confirmation`() = runTest {
        val viewModel = buildViewModel()
        viewModel.onReasonSelected(VideoReportReason.SpamOrScam)
        viewModel.onSubmitClick("video-1")
        advanceUntilIdle()

        viewModel.onDismiss()

        assertThat(viewModel.state.value).isEqualTo(ReportVideoUiState())
    }
}
