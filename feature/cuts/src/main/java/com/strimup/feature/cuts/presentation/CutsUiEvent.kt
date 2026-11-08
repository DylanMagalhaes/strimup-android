package com.strimup.feature.cuts.presentation

import androidx.annotation.StringRes
import com.strimup.core.ui.text.UiText

sealed interface CutsUiEvent {
    data class LoginRequired(@StringRes val messageRes: Int) : CutsUiEvent

    data class ShowMessage(val message: UiText) : CutsUiEvent
}
