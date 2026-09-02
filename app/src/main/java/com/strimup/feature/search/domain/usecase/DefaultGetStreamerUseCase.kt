package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultGetStreamerUseCase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamersUseCase {
    override suspend fun invoke(username: String): Result<List<Streamer>> {
        return repository.searchStreamers(username)
    }

}
