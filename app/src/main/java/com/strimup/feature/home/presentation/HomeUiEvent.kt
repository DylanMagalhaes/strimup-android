package com.strimup.feature.home.presentation

sealed interface HomeUiEvent {
    data class ShowSnackBar(val text: String) : HomeUiEvent
}