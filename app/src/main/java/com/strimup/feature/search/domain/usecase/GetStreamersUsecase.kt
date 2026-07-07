package com.strimup.feature.search.domain.usecase

import com.strimup.feature.search.domain.entity.StreamerEntity

interface GetStreamersUsecase {

    suspend operator fun invoke(username: String): Result<List<StreamerEntity>>
}