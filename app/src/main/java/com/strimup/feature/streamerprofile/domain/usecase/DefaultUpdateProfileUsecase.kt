package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.data.DefaultStreamerRepository
import com.strimup.feature.streamerprofile.data.response.StreamerResponse
import com.strimup.feature.streamerprofile.data.response.UpdateProfileResponse
import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import javax.inject.Inject

class DefaultUpdateProfileUsecase @Inject constructor(
    private val repository: StreamerRepository
): UpdateProfileUsecase{
    override suspend fun invoke(profile: StreamerProfileEntity): Result<StreamerProfileEntity> {
        return repository.updateProfile(profile)
    }
}