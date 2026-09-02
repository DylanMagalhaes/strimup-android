package com.strimup.feature.streamerdetail.presentation

import com.strimup.core.streamer.domain.entity.Streamer
sealed interface StreamerDetailUiState {
    data object Loading : StreamerDetailUiState

    data class Success(
        val streamer: Streamer,
        val isFavorite: Boolean
    ) : StreamerDetailUiState

    data class Error(
        val message: String,
    ) : StreamerDetailUiState
}
