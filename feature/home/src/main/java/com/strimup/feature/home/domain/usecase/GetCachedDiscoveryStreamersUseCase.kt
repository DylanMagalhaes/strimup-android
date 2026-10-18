package com.strimup.feature.home.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer

fun interface GetCachedDiscoveryStreamersUseCase {
    suspend operator fun invoke(): List<Streamer>
}
