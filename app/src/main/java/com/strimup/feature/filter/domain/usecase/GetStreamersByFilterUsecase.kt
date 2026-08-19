package com.strimup.feature.filter.domain.usecase

import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.feature.filter.data.mapper.toStreamerMatchRequest
import com.strimup.feature.filter.domain.entity.FilterCriteria
import javax.inject.Inject

class GetStreamersByFilterUseCase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        filter: FilterCriteria
    ): Result<StreamerMatchResult> = repository.getStreamersByFilter(filter.toStreamerMatchRequest(page))
}
