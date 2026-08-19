package com.strimup.feature.filter.data

import com.strimup.core.streamer.data.StreamerApiService
import com.strimup.feature.filter.data.mapper.toEntity
import com.strimup.feature.filter.domain.FilterOptionRepository
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity
import javax.inject.Inject

class DefaultFilterOptionsRepository @Inject constructor(
    private val service: StreamerApiService
): FilterOptionRepository {
    override suspend fun getFilterOptions(): Result<FilterOptionsEntity> {
        return runCatching {
            service.getStreamerOptions().toEntity()
        }
    }
}