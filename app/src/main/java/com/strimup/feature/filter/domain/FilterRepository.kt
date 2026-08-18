package com.strimup.feature.filter.domain

import com.strimup.feature.filter.data.local.model.FilterRoomEntity
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity

interface FilterRepository {
    suspend fun getFilters(): Result<List<com.strimup.feature.filter.domain.entity.FilterEntity>>

    suspend fun deleteFilterById(id: String): Result<Unit>

    suspend fun createFilter(
        name: String,
        criteria: FilterCriteria
    ): Result<FilterEntity>

    suspend fun getFilterById(id: String): Result<FilterEntity>
}