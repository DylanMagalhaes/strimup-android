package com.strimup.feature.streamerprofile.presentation.streamerprofile

import androidx.annotation.StringRes

sealed interface ProfileUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : ProfileUiEvent
}
