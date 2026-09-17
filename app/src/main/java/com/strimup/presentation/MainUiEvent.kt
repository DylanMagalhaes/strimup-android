package com.strimup.presentation

import androidx.annotation.StringRes

sealed interface MainUiEvent {
    data object LoggedOut : MainUiEvent
    data class ShowSnackBar(@StringRes val textRes: Int) : MainUiEvent
}
