package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.data.DefaultStreamerRepository
import javax.inject.Inject

class DefaultUpdateAvatarUsecase @Inject constructor(
    private val repository: DefaultStreamerRepository
) {
     suspend operator fun invoke(uri: String): Result<String> {
        return repository.updateAvatar(uri)
    }
}