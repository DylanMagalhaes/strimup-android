package com.strimup.feature.filter.domain.usecase

import com.strimup.feature.filter.domain.StreamerRepository
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.StreamerMatchResult
import javax.inject.Inject

class GetStreamersByFilterUseCase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        filter: FilterCriteria
    ): Result<StreamerMatchResult> = repository.getStreamersByFilter(page, filter)
}