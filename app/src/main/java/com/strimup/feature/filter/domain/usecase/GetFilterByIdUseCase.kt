package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.entity.FilterEntity
import javax.inject.Inject

class GetFilterByIdUseCase  @Inject constructor (
    private val repository: FilterRepository
){
    suspend operator fun invoke(id: String): Result <FilterEntity> = repository.getFilterById(id)
}