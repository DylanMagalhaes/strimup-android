package com.strimup.feature.home.injection

import com.strimup.feature.home.data.BannerApiService
import com.strimup.feature.home.data.DefaultBannerRepository
import com.strimup.feature.home.domain.BannerRepository
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import com.strimup.feature.home.domain.usecase.GetStreamersWithoutFavoriteUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface HomeModule {
    @Binds
    fun bindsStreamerUseCase(impl: GetStreamersWithoutFavoriteUseCase): GetStreamersUseCase

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
