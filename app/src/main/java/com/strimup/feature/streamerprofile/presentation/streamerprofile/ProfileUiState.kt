package com.strimup.feature.streamerprofile.presentation.streamerprofile

import com.strimup.core.streamer.domain.entity.Streamer

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data class Success(
        val streamer: Streamer
    ) : ProfileUiState

    data class Error(
        val errorMessage: String
    ) : ProfileUiState
}