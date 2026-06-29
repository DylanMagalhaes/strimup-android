package com.strimup.feature.auth.presentation

sealed interface UiEvent {
    data class ShowSnackBar(val text: String) : UiEvent
}