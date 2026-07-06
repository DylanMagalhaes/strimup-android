package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class DefaultSearchStreamersUsecase @Inject constructor(
    private val repository: StreamerRepository
): SearchStreamersUsecase {
    override suspend fun invoke(query: String): Result<List<StreamerEntity>> {
        return repository.searchStreamers(query)
    }

}