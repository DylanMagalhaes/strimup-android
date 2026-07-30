package com.strimup.feature.filter.domain

import com.strimup.feature.home.domain.entity.FilterEntity

interface FilterRepository {
    suspend fun getFilters(): Result<List<FilterEntity>>
}