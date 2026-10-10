package com.strimup.feature.home.data

import com.strimup.core.network.toDomainResult
import com.strimup.core.database.dao.HomeDao
import com.strimup.feature.home.data.mapper.toDomain
import com.strimup.feature.home.data.mapper.toRoomEntity
import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.entity.BannerItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultBannerRepository @Inject constructor(
    private val service: BannerApiService,
    private val homeDao: HomeDao,
) : BannerRepository {

    override fun observeBannerItems(): Flow<List<BannerItemEntity>> =
        homeDao.observeBanner().map { items -> items.map { it.toDomain() } }

    override suspend fun refreshBannerItems(): Result<Unit> {
        return runCatching {
            val items = service.getBannerItems()
            homeDao.replaceBanner(items.mapIndexed { index, item -> item.toRoomEntity(orderIndex = index) })
        }.toDomainResult()
    }
}
