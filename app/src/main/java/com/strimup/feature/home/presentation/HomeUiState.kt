package com.strimup.feature.home.presentation

import androidx.annotation.StringRes
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.FilterEntity

data class HomeUiState(
    val streamers: List<Streamer> = emptyList(),
    val currentTab: FilterEntity = FilterEntity.Discovery,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null,
    val isShowingSavedContent: Boolean = false,
    val isBannerLoading: Boolean = true,
    val bannerItems: List<BannerItemEntity> = emptyList(),
    val favoriteStreamerIds: Set<String> = emptySet(),
) {
    val shouldShowStreamersError: Boolean
        get() = !isLoading && streamers.isEmpty() && errorMessageRes != null
}
