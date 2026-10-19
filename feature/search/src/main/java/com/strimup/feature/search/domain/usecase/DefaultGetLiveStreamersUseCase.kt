package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultGetLiveStreamersUseCase @Inject constructor(
    private val repository: StreamerRepository,
) : GetLiveStreamersUseCase {
    override suspend fun invoke(): Result<List<Streamer>> = repository.getLiveStreamers(emptyList())
}
