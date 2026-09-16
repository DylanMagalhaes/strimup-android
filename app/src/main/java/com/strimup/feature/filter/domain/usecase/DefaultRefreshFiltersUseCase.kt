package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import javax.inject.Inject

class DefaultRefreshFiltersUseCase @Inject constructor(
    private val repository: FilterRepository
) : RefreshFiltersUseCase {
    override suspend fun invoke(): Result<Unit> {
        return repository.refreshFilters()
    }
}
