package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.core.user.domain.UserRepository
import javax.inject.Inject

class DefaultUpdateAvatarUseCase @Inject constructor(
    private val repository: StreamerRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uri: String): Result<String> {
        return repository.updateAvatar(uri)
            .onSuccess { avatarUrl -> userRepository.updateCurrentUserAvatar(avatarUrl) }
    }
}
