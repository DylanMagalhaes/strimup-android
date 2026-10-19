package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface GetLiveStreamersUseCase {
    suspend operator fun invoke(): Result<List<Streamer>>
}
