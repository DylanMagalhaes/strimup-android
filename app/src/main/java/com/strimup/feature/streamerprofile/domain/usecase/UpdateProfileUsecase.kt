package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.data.response.StreamerResponse
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

interface UpdateProfileUsecase {
    suspend operator fun invoke(profile: StreamerProfileEntity): Result<StreamerProfileEntity>
}