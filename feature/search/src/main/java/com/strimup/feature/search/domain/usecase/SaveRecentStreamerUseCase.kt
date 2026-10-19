package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface SaveRecentStreamerUseCase {
    suspend operator fun invoke(streamer: Streamer)
}
