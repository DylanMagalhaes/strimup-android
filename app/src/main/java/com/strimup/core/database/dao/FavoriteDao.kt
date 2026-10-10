package com.strimup.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.strimup.core.database.model.FavoriteRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    fun observeFavorites(): Flow<List<FavoriteRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteStreamer(favorite: FavoriteRoomEntity)

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteFavoriteStreamer(id: String)

    @Query("DELETE FROM favorite")
    suspend fun deleteAllFavorites()

}