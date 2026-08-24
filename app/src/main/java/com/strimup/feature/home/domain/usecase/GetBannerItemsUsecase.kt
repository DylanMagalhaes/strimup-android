package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.entity.BannerItemEntity
import javax.inject.Inject

class GetBannerItemsUsecase @Inject constructor(
    val repository: BannerRepository
) {
    suspend operator fun invoke(): Result<List<BannerItemEntity>> = repository.getBannerItems()
}