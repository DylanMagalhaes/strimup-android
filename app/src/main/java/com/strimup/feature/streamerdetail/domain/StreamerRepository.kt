package com.strimup.feature.streamerdetail.domain

import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity

interface StreamerRepository {
    suspend fun getStreamerById(id: String): Result<StreamerDetailEntity>
}