package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface GetStreamerUsecase {
    suspend operator fun invoke(id: String): Result<Streamer>
}
