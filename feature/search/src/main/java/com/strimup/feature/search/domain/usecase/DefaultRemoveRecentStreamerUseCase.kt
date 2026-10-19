package com.strimup.feature.search.domain.usecase

import com.strimup.feature.search.domain.RecentStreamerRepository
import javax.inject.Inject

class DefaultRemoveRecentStreamerUseCase @Inject constructor(
    private val repository: RecentStreamerRepository,
) : RemoveRecentStreamerUseCase {
    override suspend fun invoke(id: String) = repository.removeRecentStreamer(id)
}
