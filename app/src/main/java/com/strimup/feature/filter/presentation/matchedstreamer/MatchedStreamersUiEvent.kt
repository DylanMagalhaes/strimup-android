package com.strimup.feature.filter.presentation.matchedstreamer

sealed interface MatchedStreamersUiEvent {
    data class ShowSnackBar(val text: String) : MatchedStreamersUiEvent
}
