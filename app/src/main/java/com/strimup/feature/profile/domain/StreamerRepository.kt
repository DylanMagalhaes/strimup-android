package com.strimup.feature.profile.domain

import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity

interface StreamerRepository {
    suspend fun getStreamerById(id: String): ProfileStreamerEntity
}