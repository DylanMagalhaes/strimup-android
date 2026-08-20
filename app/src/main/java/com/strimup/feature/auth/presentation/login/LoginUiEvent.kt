package com.strimup.feature.auth.presentation.login

sealed interface LoginUiEvent {
    data class ShowSnackBar(val text: String) : LoginUiEvent
    data object ShowHomeUi : LoginUiEvent
}