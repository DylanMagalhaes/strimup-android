package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultUpdateAvatarUsecase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(uri: String): Result<String> {
        return repository.updateAvatar(uri)
    }
}
