package com.strimup.feature.home.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.strimup.feature.home.data.local.model.HomeBannerRoomEntity
import com.strimup.feature.home.data.local.model.HomeStreamerRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {

    @Query("SELECT * FROM home_banner ORDER BY orderIndex")
    fun observeBanner(): Flow<List<HomeBannerRoomEntity>>

    @Query("SELECT * FROM home_streamers ORDER BY orderIndex")
    fun observeStreamers(): Flow<List<HomeStreamerRoomEntity>>

    @Transaction
    suspend fun replaceBanner(items: List<HomeBannerRoomEntity>) {
        deleteBanner()
        insertBanner(items)
    }

    @Transaction
    suspend fun replaceStreamers(streamers: List<HomeStreamerRoomEntity>) {
        deleteStreamers()
        insertStreamers(streamers)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(items: List<HomeBannerRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreamers(streamers: List<HomeStreamerRoomEntity>)

    @Query("DELETE FROM home_banner")
    suspend fun deleteBanner()

    @Query("DELETE FROM home_streamers")
    suspend fun deleteStreamers()
}
