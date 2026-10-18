package com.strimup.feature.auth.presentation.oauthonboarding

import androidx.annotation.StringRes

sealed interface OAuthOnboardingUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : OAuthOnboardingUiEvent
    data object Completed : OAuthOnboardingUiEvent
}
