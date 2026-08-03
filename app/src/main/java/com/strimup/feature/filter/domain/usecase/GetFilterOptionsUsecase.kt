package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterOptionRepository
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity
import javax.inject.Inject

class GetFilterOptionsUsecase @Inject constructor(
    private val repository: FilterOptionRepository
) {
    suspend operator fun invoke(): Result<FilterOptionsEntity> = repository.getFilterOptions()
}