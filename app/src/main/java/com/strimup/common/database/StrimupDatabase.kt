package com.strimup.common.database


import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.strimup.common.database.converter.CommonConverters
import com.strimup.common.user.data.local.dao.UserDao
import com.strimup.common.user.data.local.model.UserRoomEntity
import com.strimup.feature.filter.data.local.dao.FilterDao
import com.strimup.feature.filter.data.local.model.FilterRoomEntity

@Database(
    entities = [
        UserRoomEntity::class,
        FilterRoomEntity::class
    ],
    version = 1,
    exportSchema = false
)
@ColumnTypeConverters(CommonConverters::class)
abstract class StrimupDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun filterDao(): FilterDao
}