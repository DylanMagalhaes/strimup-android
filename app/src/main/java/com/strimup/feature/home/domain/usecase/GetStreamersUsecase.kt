package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity

fun interface GetStreamersUsecase {
    suspend operator fun invoke(filter: FilterEntity): Result<List<StreamerEntity>>
}

