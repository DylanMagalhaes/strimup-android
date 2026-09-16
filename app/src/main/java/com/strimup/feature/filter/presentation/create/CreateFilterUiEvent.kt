package com.strimup.feature.filter.presentation.create

import androidx.annotation.StringRes

sealed interface CreateFilterUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : CreateFilterUiEvent
    data object FilterCreated : CreateFilterUiEvent
}
