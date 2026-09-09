package com.strimup.feature.filter.domain.usecase

import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.feature.filter.domain.entity.FilterCriteria

fun interface GetStreamersByFilterUseCase {
    suspend operator fun invoke(
        page: Int,
        filter: FilterCriteria
    ): Result<StreamerMatchResult>
}
