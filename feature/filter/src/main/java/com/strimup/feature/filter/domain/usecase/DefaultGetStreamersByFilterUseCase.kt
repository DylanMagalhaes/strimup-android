package com.strimup.feature.filter.domain.usecase

import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.repository.StreamerRepository
import com.strimup.feature.filter.data.mapper.toStreamerMatchRequest
import com.strimup.feature.filter.domain.entity.FilterCriteria
import javax.inject.Inject

class DefaultGetStreamersByFilterUseCase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamersByFilterUseCase {
    override suspend fun invoke(
        page: Int,
        filter: FilterCriteria
    ): Result<StreamerMatchResult> = repository.getStreamersByFilter(filter.toStreamerMatchRequest(page))
}
