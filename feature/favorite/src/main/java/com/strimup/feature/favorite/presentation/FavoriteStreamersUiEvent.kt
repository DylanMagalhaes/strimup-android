package com.strimup.feature.favorite.presentation

import androidx.annotation.StringRes

sealed interface FavoriteStreamersUiEvent {
    data class ShowSnackBar(@StringRes val textRes: Int) : FavoriteStreamersUiEvent
}
