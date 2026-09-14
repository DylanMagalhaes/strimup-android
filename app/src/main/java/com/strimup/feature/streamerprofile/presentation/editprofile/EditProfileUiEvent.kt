package com.strimup.feature.streamerprofile.presentation.editprofile

sealed interface EditProfileUiEvent {
    data class ShowSnackBar(val text: String) : EditProfileUiEvent
}
