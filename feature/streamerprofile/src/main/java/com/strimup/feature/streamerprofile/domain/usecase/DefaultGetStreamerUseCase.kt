package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultGetStreamerUseCase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamerUseCase {
    override suspend fun invoke(id: String): Result<Streamer> {
        return repository.getStreamerById(id)
    }
}
