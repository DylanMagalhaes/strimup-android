package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import javax.inject.Inject

class DefaultUpdateProfileUsecase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(profile: StreamerProfileEntity): Result<StreamerProfileEntity> {
        return repository.updateProfile(profile)
    }
}