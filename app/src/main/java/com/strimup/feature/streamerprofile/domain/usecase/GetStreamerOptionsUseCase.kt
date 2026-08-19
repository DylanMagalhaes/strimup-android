package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class GetStreamerOptionsUseCase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(): Result<StreamerOptions> {
        return repository.getStreamerOptions()
    }
}
