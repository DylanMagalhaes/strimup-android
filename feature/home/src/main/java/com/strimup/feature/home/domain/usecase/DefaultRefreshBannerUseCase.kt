package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.BannerRepository
import javax.inject.Inject

class DefaultRefreshBannerUseCase @Inject constructor(
    private val repository: BannerRepository,
) : RefreshBannerUseCase {
    override suspend fun invoke(): Result<Unit> = repository.refreshBannerItems()
}
