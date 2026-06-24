package com.strimup.feature.home.presentation

sealed interface UiEvent {
    data class ShowSnackBar(val text: String) : UiEvent
}