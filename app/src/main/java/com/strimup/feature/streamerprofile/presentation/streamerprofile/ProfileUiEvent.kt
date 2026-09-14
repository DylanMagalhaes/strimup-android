package com.strimup.feature.streamerprofile.presentation.streamerprofile

sealed interface ProfileUiEvent {
    data class ShowSnackBar(val text: String) : ProfileUiEvent
}
