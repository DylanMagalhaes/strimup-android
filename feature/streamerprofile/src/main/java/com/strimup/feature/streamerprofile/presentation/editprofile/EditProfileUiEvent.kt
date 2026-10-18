package com.strimup.feature.streamerprofile.presentation.editprofile

import com.strimup.core.ui.text.UiText

sealed interface EditProfileUiEvent {
    data class ShowSnackBar(val message: UiText) : EditProfileUiEvent
    data object ProfileSaved : EditProfileUiEvent
}
