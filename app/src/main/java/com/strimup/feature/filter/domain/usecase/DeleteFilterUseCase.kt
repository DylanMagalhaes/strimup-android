package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import javax.inject.Inject

class DeleteFilterUseCase @Inject constructor(
    private val repository: FilterRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteFilterById(id)
}