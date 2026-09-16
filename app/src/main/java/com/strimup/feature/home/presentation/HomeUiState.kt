package com.strimup.feature.home.presentation

import androidx.annotation.StringRes
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.FilterEntity

data class HomeUiState(
    val streamers: List<Streamer> = emptyList(),
    val currentTab: FilterEntity = FilterEntity.Discovery,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val isBannerLoading: Boolean = true,
    val bannerItems: List<BannerItemEntity> = emptyList()
)