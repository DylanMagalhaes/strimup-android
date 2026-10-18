package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import javax.inject.Inject

class DefaultGetMyVideosUseCase @Inject constructor(
    private val repository: StreamerVideoRepository,
) : GetMyVideosUseCase {
    override suspend fun invoke(): Result<List<Streamer.Video>> = repository.getMyVideos()
}
