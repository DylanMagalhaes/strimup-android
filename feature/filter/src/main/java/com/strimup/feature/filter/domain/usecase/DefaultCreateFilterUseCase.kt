package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import javax.inject.Inject

class DefaultCreateFilterUseCase @Inject constructor(
    private val repository: FilterRepository
) : CreateFilterUseCase {
    override suspend fun invoke(
        name: String,
        criteria: FilterCriteria
    ): Result<FilterEntity> {
        return repository.createFilter(name, criteria)
    }
}
