package com.strimup.core.database.injection

import android.content.Context
import androidx.room3.Room
import com.strimup.BuildConfig
import com.strimup.core.database.STRIMUP_DATABASE_NAME
import com.strimup.core.database.StrimupDatabase
import com.strimup.core.database.StrimupDatabaseMigrations
import com.strimup.core.favorite.data.local.dao.FavoriteDao
import com.strimup.core.user.data.local.dao.UserDao
import com.strimup.feature.filter.data.local.dao.FilterDao
import com.strimup.feature.home.data.local.dao.HomeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class) object DatabaseModule {

    @Provides @Singleton fun provideStrimupDatabase(
        @ApplicationContext context: Context
    ): StrimupDatabase {
        val builder = Room.databaseBuilder(context, StrimupDatabase::class.java, STRIMUP_DATABASE_NAME)

        StrimupDatabaseMigrations.all.forEach { migration -> builder.addMigrations(migration) }

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration(dropAllTables = true)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: StrimupDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: StrimupDatabase): FavoriteDao{
        return database.favoritesDao()
    }

    @Provides
    @Singleton
    fun provideHomeDao(database: StrimupDatabase): HomeDao {
        return database.homeDao()
    }

    @Provides
    @Singleton
    fun provideFilterDao(database: StrimupDatabase): FilterDao {
        return database.filterDao()
    }
}