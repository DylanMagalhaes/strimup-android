package com.strimup.feature.favorite.presentation

sealed interface FavoriteStreamersUiEvent {
    data class ShowSnackBar(val text: String) : FavoriteStreamersUiEvent
}
