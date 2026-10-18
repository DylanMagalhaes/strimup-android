package com.strimup.feature.streamerdetail.presentation

import androidx.annotation.StringRes

sealed interface StreamerDetailUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : StreamerDetailUiEvent
}
