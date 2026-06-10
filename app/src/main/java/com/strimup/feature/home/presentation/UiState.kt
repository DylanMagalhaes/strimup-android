package com.strimup.feature.home.presentation

import androidx.compose.runtime.Stable
import com.strimup.feature.home.domain.entity.StreamerEntity

@Stable
data class UiState(
    val streamers: List<StreamerEntity> = emptyList(),
    val isInLiveSelected: Boolean = false,
    val isDiscoverySelected: Boolean = true,
    val loading: Boolean = true,
)
