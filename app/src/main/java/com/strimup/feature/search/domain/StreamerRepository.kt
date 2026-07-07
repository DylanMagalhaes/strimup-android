package com.strimup.feature.search.domain

import com.strimup.feature.search.domain.entity.StreamerEntity

interface StreamerRepository {
    suspend fun getStreamers(userName: String): Result<List<StreamerEntity>>
}