package com.strimup.feature.home.presentation

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.FilterEntity

data class HomeUiState(
    val streamers: List<Streamer> = emptyList(),
    val currentTab: FilterEntity = FilterEntity.Discovery,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)