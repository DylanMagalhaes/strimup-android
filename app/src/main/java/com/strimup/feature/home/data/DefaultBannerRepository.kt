package com.strimup.feature.home.data

import com.strimup.feature.home.data.mapper.toDomain
import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.entity.BannerItemEntity
import javax.inject.Inject

class DefaultBannerRepository @Inject constructor(
    val service: BannerApiService
) : BannerRepository {
    override suspend fun getBannerItems(): Result<List<BannerItemEntity>> {
        return runCatching {
            service.getBannerItems().map { item ->
                item.toDomain()
            }
        }
    }

}