package com.strimup.feature.home.data.local

import com.strimup.core.database.dao.HomeDao
import com.strimup.core.database.model.HomeBannerRoomEntity
import com.strimup.core.database.model.HomeStreamerRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeHomeDao : HomeDao {
    val banner = MutableStateFlow<List<HomeBannerRoomEntity>>(emptyList())
    val streamers = MutableStateFlow<List<HomeStreamerRoomEntity>>(emptyList())

    override fun observeBanner(): Flow<List<HomeBannerRoomEntity>> =
        banner.map { items -> items.sortedBy { it.orderIndex } }

    override suspend fun getStreamers(): List<HomeStreamerRoomEntity> =
        streamers.value.sortedBy { it.orderIndex }

    override suspend fun insertBanner(items: List<HomeBannerRoomEntity>) {
        banner.value = banner.value + items
    }

    override suspend fun insertStreamers(streamers: List<HomeStreamerRoomEntity>) {
        this.streamers.value = this.streamers.value + streamers
    }

    override suspend fun deleteBanner() {
        banner.value = emptyList()
    }

    override suspend fun deleteStreamers() {
        streamers.value = emptyList()
    }
}
