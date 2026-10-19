package com.strimup.feature.search.domain

import com.strimup.core.streamer.domain.entity.Streamer
import kotlinx.coroutines.flow.Flow

const val RECENT_STREAMERS_LIMIT = 5

interface RecentStreamerRepository {
    fun observeRecentStreamers(): Flow<List<Streamer>>

    suspend fun saveRecentStreamer(streamer: Streamer)

    suspend fun removeRecentStreamer(id: String)
}
