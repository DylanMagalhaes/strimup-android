package com.strimup.feature.auth.presentation.register

import androidx.annotation.StringRes

sealed interface RegisterUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : RegisterUiEvent
    data object ShowHomeUi : RegisterUiEvent
}
