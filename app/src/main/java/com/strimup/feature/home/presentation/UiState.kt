package com.strimup.feature.home.presentation

import androidx.compose.runtime.Stable
import com.strimup.feature.home.domain.entity.StreamerEntity
import com.strimup.feature.home.presentation.model.HomeTab

@Stable
data class UiState(
    val streamers: List<StreamerEntity> = emptyList(),
    val currentTab: HomeTab = HomeTab.DISCOVERY,
    val loading: Boolean = true,
)
