package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface AddVideoLinkUseCase {
    suspend operator fun invoke(url: String, title: String, description: String): Result<Streamer.Video>
}
