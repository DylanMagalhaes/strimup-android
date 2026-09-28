package com.strimup.presentation

import androidx.annotation.StringRes

sealed interface MainUiEvent {
    data object OAuthLoggedIn : MainUiEvent
    data class OAuthOnboardingRequired(val tmp: String) : MainUiEvent
    data class ShowSnackBar(@StringRes val textRes: Int) : MainUiEvent
}
