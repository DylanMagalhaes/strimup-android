package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.local.dao.FilterDao
import com.strimup.feature.filter.data.mapper.toDomain
import com.strimup.feature.filter.data.mapper.toDomainEntity
import com.strimup.feature.filter.data.mapper.toDto
import com.strimup.feature.filter.data.mapper.toRoomEntity
import com.strimup.feature.filter.data.request.CreateFilterRequest
import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import javax.inject.Inject

class DefaultFilterRepository @Inject constructor(
    private val service: FilterApiService,
    private val filterDao: FilterDao
) : FilterRepository {

    override suspend fun getFilters(): Result<List<FilterEntity>> {
        return runCatching {
            val remoteFilters = service.getFilters().map { it.toDomain() }

            remoteFilters.forEach { filter ->
                filterDao.insertFilter(filter.toRoomEntity())
            }

            remoteFilters
        }.recoverCatching { throwable ->
            val localEntities = filterDao.getAllFiltersOnce()

            if (localEntities.isNotEmpty()) {
                localEntities.map { it.toDomainEntity() }
            } else {
                throw throwable
            }
        }
    }

    override suspend fun deleteFilterById(id: String): Result<Unit> {
        return runCatching {
             service.deleteFilterById(id)
            filterDao.deleteFilter(id)
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
            val newFilter = service.createFilter(request).toDomain()

            filterDao.insertFilter(newFilter.toRoomEntity())

            newFilter
        }
    }
}