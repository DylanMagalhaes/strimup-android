package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

data class StreamerDetailResult(
    val streamer: Streamer,
    val isFavorite: Boolean
)

fun interface GetStreamerUseCase {
    suspend operator fun invoke(id: String): Result<StreamerDetailResult>
}