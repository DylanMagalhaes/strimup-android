package com.strimup.core.favorite.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface GetFavoriteStreamerUseCase {

    suspend operator fun invoke(): Result<List<Streamer>>
}