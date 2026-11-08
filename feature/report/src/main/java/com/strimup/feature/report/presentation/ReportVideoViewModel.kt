package com.strimup.feature.report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.common.DomainError
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.feature.report.R
import com.strimup.feature.report.domain.entity.InvalidVideoReportException
import com.strimup.feature.report.domain.entity.VideoReportOutcome
import com.strimup.feature.report.domain.entity.VideoReportReason
import com.strimup.feature.report.domain.usecase.ReportVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportVideoViewModel @Inject constructor(
    private val reportVideo: ReportVideoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ReportVideoUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<ReportVideoUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onReasonSelected(reason: VideoReportReason) {
        _state.update { it.copy(selectedReason = reason, errorMessage = null) }
    }

    fun onDetailsChange(details: String) {
        _state.update { it.copy(details = details, errorMessage = null) }
    }

    fun onSubmitClick(videoId: String) {
        val currentState = _state.value
        val reason = currentState.selectedReason
        if (!currentState.isSubmitEnabled || reason == null) return

        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            reportVideo(videoId, reason, currentState.details)
                .onSuccess { outcome -> onReportOutcome(outcome) }
                .onFailure { exception -> onReportFailure(exception) }
        }
    }

    fun onDismiss() {
        if (!_state.value.isSubmitting) {
            _state.value = ReportVideoUiState()
        }
    }

    private suspend fun onReportOutcome(outcome: VideoReportOutcome) {
        when (outcome) {
            VideoReportOutcome.Submitted -> showConfirmation(R.string.report_video_sent)
            VideoReportOutcome.AlreadyReported -> showConfirmation(R.string.report_video_already_sent)
            VideoReportOutcome.VideoUnavailable -> closeWith(ReportVideoUiEvent.VideoUnavailable)
        }
    }

    private suspend fun onReportFailure(exception: Throwable) {
        if (exception.toDomainError() == DomainError.Unauthorized) {
            closeWith(ReportVideoUiEvent.LoginRequired)
            return
        }
        _state.update { it.copy(isSubmitting = false, errorMessage = exception.toReportErrorUiText()) }
    }

    private fun showConfirmation(messageRes: Int) {
        _state.value = ReportVideoUiState(confirmationRes = messageRes)
    }

    private suspend fun closeWith(event: ReportVideoUiEvent) {
        _state.value = ReportVideoUiState()
        _events.send(event)
    }

    private fun Throwable.toReportErrorUiText(): UiText = when (this) {
        is InvalidVideoReportException -> UiText.Resource(R.string.report_details_required)
        else -> toDomainError().toUiText()
    }
}
