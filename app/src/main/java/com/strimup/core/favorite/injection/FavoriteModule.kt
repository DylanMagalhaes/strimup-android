package com.strimup.core.favorite.injection

import com.strimup.core.favorite.data.DefaultFavoriteStreamerRepository
import com.strimup.core.favorite.data.FavoriteApiService
import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface FavoriteModule {

    @Binds
    @Singleton
    fun bindFavoriteStreamerRepository(impl: DefaultFavoriteStreamerRepository): FavoriteStreamerRepository

    companion object {
        @Provides
        @Singleton
        fun providesFavoriteApiService(retrofit: Retrofit): FavoriteApiService {
            return retrofit.create(FavoriteApiService::class.java)
        }
    }
}