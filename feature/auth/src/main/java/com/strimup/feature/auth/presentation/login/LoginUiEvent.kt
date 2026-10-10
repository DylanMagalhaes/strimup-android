package com.strimup.feature.auth.presentation.login

import com.strimup.core.ui.text.UiText

sealed interface LoginUiEvent {
    data class ShowSnackBar(val message: UiText) : LoginUiEvent
    data class OpenCustomTab(val url: String) : LoginUiEvent
    data object ShowHomeUi : LoginUiEvent
}
