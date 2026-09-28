package com.strimup.feature.auth.presentation.login

import androidx.annotation.StringRes

sealed interface LoginUiEvent {
    data class ShowSnackBar(val text: String) : LoginUiEvent
    data class ShowSnackBarRes(@StringRes val textRes: Int) : LoginUiEvent
    data class OpenCustomTab(val url: String) : LoginUiEvent
    data object ShowHomeUi : LoginUiEvent
}
