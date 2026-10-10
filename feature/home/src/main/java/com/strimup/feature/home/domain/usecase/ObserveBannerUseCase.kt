package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.entity.BannerItemEntity
import kotlinx.coroutines.flow.Flow

fun interface ObserveBannerUseCase {
    operator fun invoke(): Flow<List<BannerItemEntity>>
}
