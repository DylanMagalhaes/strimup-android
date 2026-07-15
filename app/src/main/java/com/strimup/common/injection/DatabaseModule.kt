package com.strimup.common.injection

import android.content.Context
import androidx.room3.Room

import com.strimup.common.database.StrimupDatabase
import com.strimup.common.user.data.local.dao.UserDao
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
        return Room.databaseBuilder(
            context, StrimupDatabase::class.java, "strimup_database"
        )
            // .fallbackToDestructiveMigration() // À activer temporairement en dev pour modifier les entités Room sans faire de fichier de migration
            .build()
    }

    @Provides @Singleton fun provideUserDao(database: StrimupDatabase): UserDao {
        return database.userDao()
    }
}