package com.strimup.core.favorite.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import kotlinx.coroutines.flow.Flow

fun interface ObserveFavoritesStreamersUseCase {
    operator fun invoke(): Flow<List<Streamer>>
}