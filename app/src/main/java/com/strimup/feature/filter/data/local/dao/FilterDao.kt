package com.strimup.feature.filter.data.local.dao


import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.strimup.feature.filter.data.local.model.FilterRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Query("SELECT * FROM filters WHERE id = :id")
    suspend fun getFilterById(id: String): FilterRoomEntity?

    @Query("SELECT * FROM filters")
    fun getAllFilters(): Flow<List<FilterRoomEntity>>

    @Query("SELECT * FROM filters")
    suspend fun getAllFiltersOnce(): List<FilterRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: FilterRoomEntity)

    @Query("DELETE FROM filters WHERE id = :id")
    suspend fun deleteFilter(id: String)

}