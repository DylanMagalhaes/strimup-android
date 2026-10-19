package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.search.domain.RecentStreamerRepository
import javax.inject.Inject

class DefaultSaveRecentStreamerUseCase @Inject constructor(
    private val repository: RecentStreamerRepository,
) : SaveRecentStreamerUseCase {
    override suspend fun invoke(streamer: Streamer) = repository.saveRecentStreamer(streamer)
}
