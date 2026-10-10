package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity

fun interface CreateFilterUseCase {
    suspend operator fun invoke(
        name: String,
        criteria: FilterCriteria
    ): Result<FilterEntity>
}
