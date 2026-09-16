package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultObserveFiltersUseCase @Inject constructor(
    private val repository: FilterRepository
) : ObserveFiltersUseCase {
    override fun invoke(): Flow<List<FilterEntity>> {
        return repository.observeFilters()
    }
}
