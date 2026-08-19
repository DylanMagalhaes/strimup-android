package com.strimup.feature.filter.presentation.matchedstreamer

import com.strimup.core.streamer.domain.entity.StreamerMatchResult

data class UiState(
    val isLoading: Boolean = false,
    val matchedResult: StreamerMatchResult? = null,
    val originalMatchedResult: StreamerMatchResult? = null,
    val isLiveOnly: Boolean = false,
    val filterName: String? = null,
    val errorMessage: String? = null
)