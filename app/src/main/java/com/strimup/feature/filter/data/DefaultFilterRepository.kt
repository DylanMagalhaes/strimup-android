package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.mapper.toDomain
import com.strimup.feature.filter.data.mapper.toDto
import com.strimup.feature.filter.data.request.CreateFilterRequest
import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
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

    override suspend fun deleteFilterById(id: String): Result<Unit> {
        return runCatching {
            service.deleteFilterById(id)
        }
    }

    override suspend fun createFilter(
        name: String,
        criteria: FilterCriteria
    ): Result<FilterEntity> {
        return runCatching {
            val request = CreateFilterRequest(
                name = name,
                filterJson = criteria.toDto()
            )
            service.createFilter(request).toDomain()
        }
    }

}