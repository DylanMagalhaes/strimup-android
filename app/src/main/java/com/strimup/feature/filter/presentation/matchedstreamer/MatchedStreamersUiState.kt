package com.strimup.feature.filter.presentation.matchedstreamer

import com.strimup.core.streamer.domain.entity.StreamerMatchResult

sealed interface MatchedStreamersUiState {
    data object Loading : MatchedStreamersUiState

    data class Success(
        val filterName: String? = null,
        val matchedResult: StreamerMatchResult,
        val originalMatchedResult: StreamerMatchResult,
        val isLiveOnly: Boolean = false,
        val isLoadingNextPage: Boolean = false
    ) : MatchedStreamersUiState

    data class Error(
        val errorMessage: String
    ) : MatchedStreamersUiState
}