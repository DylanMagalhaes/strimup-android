package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.entity.FilterEntity
import kotlinx.coroutines.flow.Flow

interface ObserveFiltersUseCase {
    operator fun invoke(): Flow<List<FilterEntity>>
}
