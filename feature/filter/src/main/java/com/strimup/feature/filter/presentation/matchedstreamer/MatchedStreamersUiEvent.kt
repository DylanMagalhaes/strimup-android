package com.strimup.feature.filter.presentation.matchedstreamer

import androidx.annotation.StringRes

sealed interface MatchedStreamersUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : MatchedStreamersUiEvent
}
