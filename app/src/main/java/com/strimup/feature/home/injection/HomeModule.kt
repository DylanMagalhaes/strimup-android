package com.strimup.feature.home.injection

import com.strimup.feature.home.data.BannerApiService
import com.strimup.feature.home.data.DefaultBannerRepository
import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.usecase.GetStreamersUsecase
import com.strimup.feature.home.domain.usecase.GetStreamersWithoutFavoriteUsecase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface HomeModule {
    @Binds
    fun bindsStreamerUsecase(impl: GetStreamersWithoutFavoriteUsecase): GetStreamersUsecase

    @Binds
    fun bindBannerRepository(impl: DefaultBannerRepository): BannerRepository

    companion object {
        @Provides
        @Singleton
        fun providesBannerApiService(retrofit: Retrofit): BannerApiService {
            return retrofit.create(BannerApiService::class.java)
        }
    }
}
