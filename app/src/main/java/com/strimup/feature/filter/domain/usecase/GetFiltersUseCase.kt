package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.entity.FilterEntity

fun interface GetFiltersUseCase {
    suspend operator fun invoke(): Result<List<FilterEntity>>
}
