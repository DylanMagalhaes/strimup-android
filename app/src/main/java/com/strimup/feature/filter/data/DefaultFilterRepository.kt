package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.mapper.toDomain
import com.strimup.feature.filter.domain.FilterRepository
import javax.inject.Inject

class DefaultFilterRepository @Inject constructor(
    private val service: FilterApiService
) : FilterRepository {
    override suspend fun getFilters(): Result<List<com.strimup.feature.filter.domain.entity.FilterEntity>> {
        return runCatching {
            service.getFilters().map {
                it.toDomain()
            }
        }
    }

}