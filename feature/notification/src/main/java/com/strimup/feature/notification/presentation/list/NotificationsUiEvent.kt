package com.strimup.feature.notification.presentation.list

import androidx.annotation.StringRes

sealed interface NotificationsUiEvent {
    data class ShowSnackBar(@param:StringRes val textRes: Int) : NotificationsUiEvent
}
