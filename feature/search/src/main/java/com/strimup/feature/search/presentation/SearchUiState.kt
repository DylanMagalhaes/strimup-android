package com.strimup.feature.search.presentation

import androidx.annotation.StringRes
import com.strimup.core.streamer.domain.entity.Streamer

sealed interface SearchUiState {
    val searchQuery: String

    data class Content(
        override val searchQuery: String = "",
        val streamers: List<Streamer> = emptyList(),
    ) : SearchUiState

    data class Loading(
        override val searchQuery: String,
    ) : SearchUiState

    data class Empty(
        override val searchQuery: String,
    ) : SearchUiState

    data class Error(
        override val searchQuery: String,
        @StringRes val messageRes: Int,
    ) : SearchUiState
}