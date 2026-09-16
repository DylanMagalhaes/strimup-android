package com.strimup.feature.filter.domain

import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import kotlinx.coroutines.flow.Flow

interface FilterRepository {
    fun observeFilters(): Flow<List<com.strimup.feature.filter.domain.entity.FilterEntity>>

    suspend fun refreshFilters(): Result<Unit>

    suspend fun deleteFilterById(id: String): Result<Unit>

    suspend fun createFilter(
        name: String,
        criteria: FilterCriteria
    ): Result<FilterEntity>

    suspend fun getFilterById(id: String): Result<FilterEntity>
}