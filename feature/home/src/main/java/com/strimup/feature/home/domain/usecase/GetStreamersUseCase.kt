package com.strimup.feature.home.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.FilterEntity

fun interface GetStreamersUseCase {
    suspend operator fun invoke(filter: FilterEntity): Result<List<Streamer>>
}
