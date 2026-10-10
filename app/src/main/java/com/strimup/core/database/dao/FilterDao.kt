package com.strimup.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.strimup.core.database.model.FilterRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Query("SELECT * FROM filters WHERE id = :id")
    suspend fun getFilterById(id: String): FilterRoomEntity?

    @Query("SELECT * FROM filters")
    fun observeFilters(): Flow<List<FilterRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: FilterRoomEntity)

    @Query("DELETE FROM filters WHERE id = :id")
    suspend fun deleteFilter(id: String)

    @Query("DELETE FROM filters")
    suspend fun deleteAllFilters()

}