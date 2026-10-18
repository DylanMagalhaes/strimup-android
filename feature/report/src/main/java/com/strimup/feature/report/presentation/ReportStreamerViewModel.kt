package com.strimup.feature.report.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.feature.report.R
import com.strimup.feature.report.domain.entity.InvalidStreamerReportException
import com.strimup.feature.report.domain.entity.ReportOutcome
import com.strimup.feature.report.domain.entity.ReportReason
import com.strimup.feature.report.domain.entity.StreamerReportPolicy
import com.strimup.feature.report.domain.usecase.ReportStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportStreamerViewModel @Inject constructor(
    private val reportStreamer: ReportStreamerUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ReportStreamerUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<ReportStreamerUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onReasonSelected(reason: ReportReason) {
        _state.update { it.copy(selectedReason = reason, errorMessage = null) }
    }

    fun onDetailsChange(details: String) {
        _state.update {
            it.copy(details = details.take(StreamerReportPolicy.MAX_DETAILS_LENGTH), errorMessage = null)
        }
    }

    fun onSubmitClick(streamerId: String) {
        val currentState = _state.value
        val reason = currentState.selectedReason
        if (!currentState.isSubmitEnabled || reason == null) return

        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            reportStreamer(streamerId, reason, currentState.details)
                .onSuccess { outcome ->
                    _state.value = ReportStreamerUiState()
                    _events.send(ReportStreamerUiEvent.Reported(outcome.messageRes()))
                }
                .onFailure { exception ->
                    _state.update { it.copy(isSubmitting = false, errorMessage = exception.toReportErrorUiText()) }
                }
        }
    }

    fun onDismiss() {
        if (!_state.value.isSubmitting) {
            _state.value = ReportStreamerUiState()
        }
    }

    @StringRes
    private fun ReportOutcome.messageRes(): Int = when (this) {
        ReportOutcome.Submitted -> R.string.report_sent
        ReportOutcome.AlreadyReported -> R.string.report_already_sent
    }

    private fun Throwable.toReportErrorUiText(): UiText = when (this) {
        is InvalidStreamerReportException -> UiText.Resource(R.string.report_details_required)
        else -> toDomainError().toUiText()
    }
}
