package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface GetMyVideosUseCase {
    suspend operator fun invoke(): Result<List<Streamer.Video>>
}
