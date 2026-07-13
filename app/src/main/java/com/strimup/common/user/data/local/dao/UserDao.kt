package com.strimup.common.user.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.strimup.common.user.data.local.model.UserRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<UserRoomEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserRoomEntity)

    @Query("DELETE FROM users")
    suspend fun clearUser()
}