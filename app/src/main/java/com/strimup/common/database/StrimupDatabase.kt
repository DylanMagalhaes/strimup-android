package com.strimup.common.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.strimup.common.user.data.local.dao.UserDao
import com.strimup.common.user.data.local.model.UserRoomEntity

@Database(
    entities = [UserRoomEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StrimupDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}