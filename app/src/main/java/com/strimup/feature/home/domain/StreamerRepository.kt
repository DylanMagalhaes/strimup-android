package com.strimup.feature.home.domain

import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity

interface StreamerRepository {
    suspend fun getStreamers(
        filter: FilterEntity,
        favoriteStreamerIds: List<String>
    ): Result<List<StreamerEntity>>

    suspend fun getFavoriteStreamerIds(): List<String>

    suspend fun searchStreamers(query: String): Result<List<StreamerEntity>>
}