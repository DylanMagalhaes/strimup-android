package com.strimup.core.database.dao


import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.strimup.core.database.model.UserRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<UserRoomEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserRoomEntity)

    @Query("UPDATE users SET imageUrl = :avatarUrl")
    suspend fun updateAvatarUrl(avatarUrl: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
