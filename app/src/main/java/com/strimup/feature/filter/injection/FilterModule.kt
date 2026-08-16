package com.strimup.feature.filter.injection

import com.strimup.feature.filter.data.DefaultFilterOptionsRepository
import com.strimup.feature.filter.data.DefaultFilterRepository
import com.strimup.feature.filter.data.FilterApiService
import com.strimup.feature.filter.data.StreamerApiService
import com.strimup.feature.filter.data.DefaultStreamerRepository
import com.strimup.feature.filter.domain.FilterOptionRepository
import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.StreamerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface FilterModule {

    @Binds
    @Singleton
    fun bindFilterRepository(impl: DefaultFilterRepository): FilterRepository

    @Binds
    @Singleton
    fun bindFilterOptionRepository(impl: DefaultFilterOptionsRepository): FilterOptionRepository

    @Binds
    @Singleton
    fun bindStreamerRepository(impl: DefaultStreamerRepository): StreamerRepository

    companion object {
        @Provides
        @Singleton
        fun providesFilterApiService(retrofit: Retrofit): FilterApiService {
            return retrofit.create(FilterApiService::class.java)
        }

        @Provides
        @Singleton
        fun providesStreamerApiService(retrofit: Retrofit): StreamerApiService {
            return retrofit.create(StreamerApiService::class.java)
        }
    }
}