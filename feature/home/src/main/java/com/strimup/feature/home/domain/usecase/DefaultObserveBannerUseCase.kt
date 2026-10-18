package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.entity.BannerItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultObserveBannerUseCase @Inject constructor(
    private val repository: BannerRepository,
) : ObserveBannerUseCase {
    override fun invoke(): Flow<List<BannerItemEntity>> = repository.observeBannerItems()
}
