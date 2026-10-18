package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterOptionRepository
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity
import javax.inject.Inject

class DefaultGetFilterOptionsUseCase @Inject constructor(
    private val repository: FilterOptionRepository
) : GetFilterOptionsUseCase {
    override suspend fun invoke(): Result<FilterOptionsEntity> = repository.getFilterOptions()
}
