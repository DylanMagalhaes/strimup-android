package com.strimup.feature.home.domain

import com.strimup.feature.home.domain.entity.BannerItemEntity
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    fun observeBannerItems(): Flow<List<BannerItemEntity>>

    suspend fun refreshBannerItems(): Result<Unit>
}
