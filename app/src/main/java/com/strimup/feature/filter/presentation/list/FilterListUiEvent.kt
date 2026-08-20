package com.strimup.feature.filter.presentation.list

sealed interface FilterListUiEvent {
    data class ShowSnackBar(val text: String) : FilterListUiEvent
}