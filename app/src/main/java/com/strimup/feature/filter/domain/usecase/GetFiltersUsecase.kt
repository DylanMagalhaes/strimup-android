package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.data.DefaultFilterRepository
import com.strimup.feature.filter.domain.entity.FilterEntity
import javax.inject.Inject

class GetFiltersUsecase @Inject constructor(
    private val repository: DefaultFilterRepository
) {
    suspend fun invoke(): Result<List<FilterEntity>> {
        return repository.getFilters()
    }
}