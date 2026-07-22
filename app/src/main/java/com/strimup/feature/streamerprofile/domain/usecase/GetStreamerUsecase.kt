package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

interface GetStreamerUsecase {
    suspend operator fun invoke(id: String): Result<StreamerProfileEntity>
}