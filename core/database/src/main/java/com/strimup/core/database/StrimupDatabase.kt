package com.strimup.core.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.strimup.core.database.converter.CommonConverters
import com.strimup.core.database.dao.FavoriteDao
import com.strimup.core.database.dao.FilterDao
import com.strimup.core.database.dao.HomeDao
import com.strimup.core.database.dao.UserDao
import com.strimup.core.database.model.FavoriteRoomEntity
import com.strimup.core.database.model.FilterRoomEntity
import com.strimup.core.database.model.HomeBannerRoomEntity
import com.strimup.core.database.model.HomeStreamerRoomEntity
import com.strimup.core.database.model.UserRoomEntity

@Database(
    entities = [
        UserRoomEntity::class,
        FilterRoomEntity::class,
        FavoriteRoomEntity::class,
        HomeBannerRoomEntity::class,
        HomeStreamerRoomEntity::class,
    ],
    version = STRIMUP_DATABASE_VERSION,
    exportSchema = true
)
@ColumnTypeConverters(CommonConverters::class)
abstract class StrimupDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun filterDao(): FilterDao

    abstract fun favoritesDao(): FavoriteDao

    abstract fun homeDao(): HomeDao
}
