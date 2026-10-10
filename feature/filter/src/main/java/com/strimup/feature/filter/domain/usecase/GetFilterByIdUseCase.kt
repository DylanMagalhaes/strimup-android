package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.entity.FilterEntity

fun interface GetFilterByIdUseCase {
    suspend operator fun invoke(id: String): Result<FilterEntity>
}
