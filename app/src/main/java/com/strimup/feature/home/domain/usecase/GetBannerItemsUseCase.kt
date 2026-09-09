package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.entity.BannerItemEntity
import javax.inject.Inject

class GetBannerItemsUseCase @Inject constructor(
    val repository: BannerRepository
): GetBannerUseCase {
    override suspend fun invoke(): Result<List<BannerItemEntity>> {
        return repository.getBannerItems()
    }
}