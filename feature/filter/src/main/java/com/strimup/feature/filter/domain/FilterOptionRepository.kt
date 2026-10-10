package com.strimup.feature.filter.domain

import com.strimup.feature.filter.domain.entity.FilterOptionsEntity

interface FilterOptionRepository {

    suspend fun getFilterOptions(): Result<FilterOptionsEntity>
}