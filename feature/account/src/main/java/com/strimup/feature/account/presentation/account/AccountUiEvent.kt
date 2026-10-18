package com.strimup.feature.account.presentation.account

import androidx.annotation.StringRes

sealed interface AccountUiEvent {
    data object LoggedOut : AccountUiEvent
    data class ShowSnackBar(@param:StringRes val textRes: Int) : AccountUiEvent
}
