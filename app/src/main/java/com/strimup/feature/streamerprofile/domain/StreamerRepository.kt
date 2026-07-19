package com.strimup.feature.streamerprofile.domain

import com.strimup.feature.streamerprofile.data.request.UpdateProfileRequest
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

interface StreamerRepository {
    suspend fun getStreamerById(id: String): Result<StreamerProfileEntity>

    suspend fun updateProfile(profile: StreamerProfileEntity): Result<StreamerProfileEntity>

    suspend fun getStreamerOptions(): Result<StreamerOptionsEntity>
}