package com.strimup.feature.account.presentation.deletion

sealed interface DeleteAccountUiEvent {
    data object AccountDeleted : DeleteAccountUiEvent
}
