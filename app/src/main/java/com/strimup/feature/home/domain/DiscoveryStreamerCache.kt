package com.strimup.feature.home.domain

import com.strimup.core.streamer.domain.entity.Streamer

const val DISCOVERY_CACHE_SIZE = 20

interface DiscoveryStreamerCache {
    suspend fun getStreamers(): List<Streamer>

    suspend fun saveStreamers(streamers: List<Streamer>)
}
