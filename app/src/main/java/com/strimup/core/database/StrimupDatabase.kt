package com.strimup.core.database


import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.strimup.core.database.converter.CommonConverters
import com.strimup.core.user.data.local.dao.UserDao
import com.strimup.core.user.data.local.model.UserRoomEntity
import com.strimup.core.favorite.data.local.dao.FavoriteDao
import com.strimup.core.favorite.data.local.model.FavoriteRoomEntity
import com.strimup.feature.filter.data.local.dao.FilterDao
import com.strimup.feature.filter.data.local.model.FilterRoomEntity

@Database(
    entities = [
        UserRoomEntity::class,
        FilterRoomEntity::class,
        FavoriteRoomEntity::class
    ],
    version = 1,
    exportSchema = false
)
@ColumnTypeConverters(CommonConverters::class)
abstract class StrimupDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun filterDao(): FilterDao

    abstract fun favoritesDao(): FavoriteDao
}