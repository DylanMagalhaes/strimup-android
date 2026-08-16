package com.strimup.feature.filter.presentation.create

sealed interface UiEvent {
    data object Success: UiEvent
}