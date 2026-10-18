package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import javax.inject.Inject

class DefaultDeleteFilterUseCase @Inject constructor(
    private val repository: FilterRepository
) : DeleteFilterUseCase {
    override suspend fun invoke(id: String): Result<Unit> = repository.deleteFilterById(id)
}
