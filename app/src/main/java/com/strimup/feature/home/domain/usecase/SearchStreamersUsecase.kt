package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity

interface SearchStreamersUsecase {
    suspend operator fun invoke(query: String): Result<List<StreamerEntity>>

}