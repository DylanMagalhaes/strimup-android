package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultGetStreamerUsecase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamerUsecase {
    override suspend fun invoke(id: String): Result<Streamer> {
        return repository.getStreamerById(id)
    }
}
