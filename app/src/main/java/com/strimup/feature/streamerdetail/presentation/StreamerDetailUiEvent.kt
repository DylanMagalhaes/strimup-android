package com.strimup.feature.streamerdetail.presentation

sealed interface StreamerDetailUiEvent {
    data class ShowSnackBar(val text: String) : StreamerDetailUiEvent
}
