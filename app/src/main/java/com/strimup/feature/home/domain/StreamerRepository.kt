package com.strimup.feature.home.domain

import com.strimup.feature.home.domain.entity.StreamerEntity

interface StreamerRepository {
    suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): List<StreamerEntity>

    suspend fun getInLiveStreamers(favoriteStreamerIds: List<String>): List<StreamerEntity>

    suspend fun getFavoriteStreamerIds(): List<String>
}