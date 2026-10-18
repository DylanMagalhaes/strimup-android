package com.strimup.feature.home.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.DiscoveryStreamerCache
import javax.inject.Inject

class DefaultGetCachedDiscoveryStreamersUseCase @Inject constructor(
    private val cache: DiscoveryStreamerCache,
) : GetCachedDiscoveryStreamersUseCase {
    override suspend fun invoke(): List<Streamer> = cache.getStreamers()
}
