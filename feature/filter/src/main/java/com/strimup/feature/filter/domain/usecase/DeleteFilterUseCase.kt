package com.strimup.feature.filter.domain.usecase

fun interface DeleteFilterUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}
