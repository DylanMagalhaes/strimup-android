package com.strimup.feature.home.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.feature.home.domain.DiscoveryStreamerCache
import com.strimup.feature.home.domain.entity.FilterEntity
import javax.inject.Inject

class GetStreamersWithoutFavoriteUseCase @Inject constructor(
    private val repository: StreamerRepository,
    private val discoveryCache: DiscoveryStreamerCache,
) : GetStreamersUseCase {
    override suspend fun invoke(filter: FilterEntity): Result<List<Streamer>> {
        return when (filter) {
            FilterEntity.Discovery -> repository.getRandomStreamers(emptyList())
                .onSuccess { streamers -> discoveryCache.saveStreamers(streamers) }

            FilterEntity.Live -> repository.getLiveStreamers(emptyList())
        }
    }
}
