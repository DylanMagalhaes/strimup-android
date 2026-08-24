package com.strimup.feature.home.domain

import com.strimup.feature.home.domain.entity.BannerItemEntity

interface BannerRepository {
    suspend fun getBannerItems(): Result<List<BannerItemEntity>>
}