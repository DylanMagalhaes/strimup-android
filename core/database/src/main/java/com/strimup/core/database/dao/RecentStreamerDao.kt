package com.strimup.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.strimup.core.database.model.RecentStreamerRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentStreamerDao {

    @Query("SELECT * FROM recent_streamers ORDER BY viewedAt DESC")
    fun observeRecentStreamers(): Flow<List<RecentStreamerRoomEntity>>

    @Transaction
    suspend fun saveRecentStreamer(streamer: RecentStreamerRoomEntity, limit: Int) {
        insertRecentStreamer(streamer)
        keepMostRecent(limit)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentStreamer(streamer: RecentStreamerRoomEntity)

    @Query(
        "DELETE FROM recent_streamers WHERE id NOT IN " +
            "(SELECT id FROM recent_streamers ORDER BY viewedAt DESC LIMIT :limit)"
    )
    suspend fun keepMostRecent(limit: Int)

    @Query("DELETE FROM recent_streamers WHERE id = :id")
    suspend fun deleteRecentStreamer(id: String)

    @Query("DELETE FROM recent_streamers")
    suspend fun deleteAllRecentStreamers()
}
