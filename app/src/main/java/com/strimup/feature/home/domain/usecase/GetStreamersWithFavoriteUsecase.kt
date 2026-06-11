package com.strimup.feature.home.domain.usecase

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class GetStreamersWithFavoriteUsecase @Inject constructor(
    private val repository: StreamerRepository,
) : GetStreamersUsecase {
    override suspend fun invoke(filter: FilterEntity): List<StreamerEntity> {
        val favoriteStreamerIds = repository.getFavoriteStreamerIds()

        val streamers = repository.getStreamers(FilterEntity.Live, favoriteStreamerIds)
        return streamers
    }
}