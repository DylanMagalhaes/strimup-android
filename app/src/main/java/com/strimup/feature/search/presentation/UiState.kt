package com.strimup.feature.search.presentation

import com.strimup.core.streamer.domain.entity.Streamer

data class UiState(
    val loading: Boolean = false,
    val streamers: List<Streamer> = emptyList(),
    val searchQuery: String = "",
)
