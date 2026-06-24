package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class GetStreamersWithoutFavoriteUsecase @Inject constructor(
    private val repository: StreamerRepository,
) : GetStreamersUsecase {
    override suspend fun invoke(filter: FilterEntity): Result<List<StreamerEntity>> {
        val streamers = repository.getStreamers(filter, emptyList())
        return streamers
    }
}