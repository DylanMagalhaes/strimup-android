package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultUpdateProfileUseCase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(profile: Streamer): Result<Streamer> {
        return repository.updateProfile(profile)
    }
}
