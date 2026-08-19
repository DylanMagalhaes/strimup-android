package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

interface GetStreamerUsecase {
    suspend operator fun invoke(id: String): Result<Streamer>
}
