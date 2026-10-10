package com.strimup.feature.home.data

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.database.dao.HomeDao
import com.strimup.feature.home.data.mapper.toHomeRoomEntity
import com.strimup.feature.home.data.mapper.toStreamer
import com.strimup.feature.home.domain.DISCOVERY_CACHE_SIZE
import com.strimup.feature.home.domain.DiscoveryStreamerCache
import javax.inject.Inject

class RoomDiscoveryStreamerCache @Inject constructor(
    private val homeDao: HomeDao,
) : DiscoveryStreamerCache {

    override suspend fun getStreamers(): List<Streamer> = homeDao.getStreamers().map { it.toStreamer() }

    override suspend fun saveStreamers(streamers: List<Streamer>) {
        homeDao.replaceStreamers(
            streamers
                .distinctBy { it.id }
                .take(DISCOVERY_CACHE_SIZE)
                .mapIndexed { index, streamer -> streamer.toHomeRoomEntity(orderIndex = index) }
        )
    }
}
