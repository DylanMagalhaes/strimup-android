package com.strimup.feature.home.presentation

import androidx.compose.runtime.Stable
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.common.domain.entity.StreamerEntity

@Stable
data class UiState(
    val streamers: List<StreamerEntity> = emptyList(),
    val currentTab: FilterEntity = FilterEntity.Discovery,
    val loading: Boolean = true,
)
