package com.strimup.feature.filter.presentation.list

import androidx.annotation.StringRes

sealed interface FilterListUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : FilterListUiEvent
}
