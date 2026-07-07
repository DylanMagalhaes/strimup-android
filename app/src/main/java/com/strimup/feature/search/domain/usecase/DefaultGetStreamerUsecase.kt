package com.strimup.feature.search.domain.usecase


import com.strimup.feature.search.data.DefaultStreamerRepository
import com.strimup.feature.search.domain.entity.StreamerEntity
import javax.inject.Inject

class DefaultGetStreamerUsecase @Inject constructor(
    private val repository: DefaultStreamerRepository
): GetStreamersUsecase {
    override suspend fun invoke(username: String): Result<List<StreamerEntity>> {
        return repository.getStreamers(username)
    }

}