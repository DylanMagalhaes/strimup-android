package com.strimup.feature.streamerdetail.presentation

import androidx.annotation.StringRes
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.twitchChannel

sealed interface StreamerDetailUiState {
    data object Loading : StreamerDetailUiState

    data class Success(
        val streamer: Streamer,
        val isFavorite: Boolean
    ) : StreamerDetailUiState {
        val liveTwitchChannel: String? = if (streamer.isLive) streamer.twitchChannel() else null
    }

    data class Error(
        @StringRes val messageRes: Int,
    ) : StreamerDetailUiState
}
