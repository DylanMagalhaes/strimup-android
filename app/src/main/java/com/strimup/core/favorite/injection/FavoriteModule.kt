package com.strimup.core.favorite.injection

import com.strimup.core.favorite.data.DefaultFavoriteStreamerRepository
import com.strimup.core.favorite.data.FavoriteApiService
import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.favorite.domain.usecase.AddStreamerToFavoritesUseCase
import com.strimup.core.favorite.domain.usecase.DefaultAddStreamerToFavoritesUseCase
import com.strimup.core.favorite.domain.usecase.DefaultDeleteStreamerFromFavoritesUseCase
import com.strimup.core.favorite.domain.usecase.DefaultObserveFavoritesStreamersUseCase
import com.strimup.core.favorite.domain.usecase.DefaultRefreshFavoriteStreamersUseCase
import com.strimup.core.favorite.domain.usecase.DeleteStreamerFromFavoritesUseCase
import com.strimup.core.favorite.domain.usecase.ObserveFavoritesStreamersUseCase
import com.strimup.core.favorite.domain.usecase.RefreshFavoriteStreamerUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface FavoriteModule {

    @Binds
    @Singleton
    fun bindFavoriteStreamerRepository(impl: DefaultFavoriteStreamerRepository): FavoriteStreamerRepository

    @Binds
    fun bindRefreshFavoriteStreamerUseCase(impl: DefaultRefreshFavoriteStreamersUseCase): RefreshFavoriteStreamerUseCase

    @Binds
    fun bindObserveFavoriteStreamerUseCase(impl: DefaultObserveFavoritesStreamersUseCase): ObserveFavoritesStreamersUseCase

    @Binds
    fun bindAddStreamerToFavoritesUseCase(impl: DefaultAddStreamerToFavoritesUseCase): AddStreamerToFavoritesUseCase

    @Binds
    fun bindDeleteStreamerFromFavoritesUseCase(impl: DefaultDeleteStreamerFromFavoritesUseCase): DeleteStreamerFromFavoritesUseCase


    companion object {
        @Provides
        @Singleton
        fun providesFavoriteApiService(retrofit: Retrofit): FavoriteApiService {
            return retrofit.create(FavoriteApiService::class.java)
        }
    }
}