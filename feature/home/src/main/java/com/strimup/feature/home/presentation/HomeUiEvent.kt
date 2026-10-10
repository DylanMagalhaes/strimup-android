package com.strimup.feature.home.presentation

import androidx.annotation.StringRes

sealed interface HomeUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : HomeUiEvent
}
