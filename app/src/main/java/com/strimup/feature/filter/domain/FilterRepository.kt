package com.strimup.feature.filter.domain

interface FilterRepository {
    suspend fun getFilters(): Result<List<com.strimup.feature.filter.domain.entity.FilterEntity>>
}