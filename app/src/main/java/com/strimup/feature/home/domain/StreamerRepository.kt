package com.strimup.feature.home.domain

import com.strimup.feature.home.domain.entity.StreamerEntity

interface StreamerRepository {
    suspend fun getRandomStreamers(): List<StreamerEntity>

    suspend fun getInLiveStreamers(): List<StreamerEntity>
}