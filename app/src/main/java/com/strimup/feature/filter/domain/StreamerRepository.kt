package com.strimup.feature.filter.domain

import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.StreamerMatchResult

interface StreamerRepository {
    suspend fun getStreamersByFilter(
        page: Int,
        filter: FilterCriteria
    ): Result<StreamerMatchResult>
}