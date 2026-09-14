package com.strimup.feature.streamerprofile.presentation.editprofile

import androidx.annotation.StringRes

sealed interface EditProfileUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : EditProfileUiEvent
}
