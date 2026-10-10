package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.entity.FilterOptionsEntity

fun interface GetFilterOptionsUseCase {
    suspend operator fun invoke(): Result<FilterOptionsEntity>
}
