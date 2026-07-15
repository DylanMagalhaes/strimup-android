package com.strimup.feature.streamerprofile.domain

import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

interface StreamerRepository {
    suspend fun getStreamerById(id: String): Result<StreamerProfileEntity>
}