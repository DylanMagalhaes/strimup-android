package com.strimup.feature.search.data.local

import com.strimup.core.database.dao.RecentStreamerDao
import com.strimup.core.database.model.RecentStreamerRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRecentStreamerDao : RecentStreamerDao {
    val streamers = MutableStateFlow<List<RecentStreamerRoomEntity>>(emptyList())

    override fun observeRecentStreamers(): Flow<List<RecentStreamerRoomEntity>> =
        streamers.map { items -> items.sortedByDescending { it.viewedAt } }

    override suspend fun insertRecentStreamer(streamer: RecentStreamerRoomEntity) {
        streamers.value = streamers.value.filterNot { it.id == streamer.id } + streamer
    }

    override suspend fun keepMostRecent(limit: Int) {
        streamers.value = streamers.value.sortedByDescending { it.viewedAt }.take(limit)
    }

    override suspend fun deleteRecentStreamer(id: String) {
        streamers.value = streamers.value.filterNot { it.id == id }
    }

    override suspend fun deleteAllRecentStreamers() {
        streamers.value = emptyList()
    }
}
