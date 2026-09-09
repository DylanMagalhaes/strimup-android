package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterEntity
import javax.inject.Inject

class DefaultGetFiltersUseCase @Inject constructor(
    private val repository: FilterRepository
) : GetFiltersUseCase {
    override suspend fun invoke(): Result<List<FilterEntity>> {
        return repository.getFilters()
    }
}
