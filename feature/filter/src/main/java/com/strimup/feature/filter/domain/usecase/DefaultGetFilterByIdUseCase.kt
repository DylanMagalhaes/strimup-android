package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterEntity
import javax.inject.Inject

class DefaultGetFilterByIdUseCase @Inject constructor(
    private val repository: FilterRepository
) : GetFilterByIdUseCase {
    override suspend fun invoke(id: String): Result<FilterEntity> = repository.getFilterById(id)
}
