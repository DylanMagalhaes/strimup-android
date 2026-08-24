package com.strimup.feature.filter.presentation.create

sealed interface CreateFilterUiEvent {
    data class ShowSnackBar(val text: String) : CreateFilterUiEvent
    data object FilterCreated : CreateFilterUiEvent
}