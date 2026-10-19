package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import kotlinx.coroutines.flow.Flow

fun interface ObserveRecentStreamersUseCase {
    operator fun invoke(): Flow<List<Streamer>>
}
