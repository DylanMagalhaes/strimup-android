package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.entity.BannerItemEntity

fun interface GetBannerUseCase {
    suspend operator fun invoke(): Result<List<BannerItemEntity>>
}