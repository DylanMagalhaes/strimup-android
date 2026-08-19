package com.strimup.feature.home.presentation

import androidx.compose.runtime.Stable
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.FilterEntity

@Stable
data class UiState(
    val streamers: List<Streamer> = emptyList(),
    val currentTab: FilterEntity = FilterEntity.Discovery,
    val loading: Boolean = true,
)
