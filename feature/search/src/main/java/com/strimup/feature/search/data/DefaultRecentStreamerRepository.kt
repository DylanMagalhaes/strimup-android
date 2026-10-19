package com.strimup.feature.search.data

import com.strimup.core.database.dao.RecentStreamerDao
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.search.data.mapper.toRecentStreamerRoomEntity
import com.strimup.feature.search.data.mapper.toStreamer
import com.strimup.feature.search.domain.RECENT_STREAMERS_LIMIT
import com.strimup.feature.search.domain.RecentStreamerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultRecentStreamerRepository @Inject constructor(
    private val recentStreamerDao: RecentStreamerDao,
) : RecentStreamerRepository {

    override fun observeRecentStreamers(): Flow<List<Streamer>> =
        recentStreamerDao.observeRecentStreamers().map { streamers -> streamers.map { it.toStreamer() } }

    override suspend fun saveRecentStreamer(streamer: Streamer) {
        recentStreamerDao.saveRecentStreamer(
            streamer = streamer.toRecentStreamerRoomEntity(viewedAt = System.currentTimeMillis()),
            limit = RECENT_STREAMERS_LIMIT,
        )
    }

    override suspend fun removeRecentStreamer(id: String) {
        recentStreamerDao.deleteRecentStreamer(id)
    }
}
