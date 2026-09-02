package com.strimup.core.favorite.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.strimup.core.favorite.data.local.model.FavoriteRoomEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    suspend fun getAllFavoritesOnce(): List<FavoriteRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteStreamer(favorite: FavoriteRoomEntity)

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteFavoriteStreamer(id: String)

}