package com.strimup.feature.search.presentation

import com.strimup.feature.search.domain.entity.StreamerEntity

data class UiState(
    val loading: Boolean = false,
    val streamers: List<StreamerEntity> = emptyList(),
    val searchQuery: String = "",
)
