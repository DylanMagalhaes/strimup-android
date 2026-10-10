package com.strimup.feature.filter.domain.usecase

fun interface RefreshFiltersUseCase {
    suspend operator fun invoke(): Result<Unit>
}
