package com.strimup.feature.home.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.feature.home.domain.entity.FilterEntity
import javax.inject.Inject

class GetStreamersWithoutFavoriteUsecase @Inject constructor(
    private val repository: StreamerRepository,
) : GetStreamersUsecase {
    override suspend fun invoke(filter: FilterEntity): Result<List<Streamer>> {
        return when (filter) {
            FilterEntity.Discovery -> repository.getRandomStreamers(emptyList())
            FilterEntity.Live -> repository.getLiveStreamers(emptyList())
        }
    }
}