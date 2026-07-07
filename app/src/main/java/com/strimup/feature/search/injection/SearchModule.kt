package com.strimup.feature.search.injection

import com.strimup.feature.search.data.DefaultStreamerRepository
import com.strimup.feature.search.data.StreamerApiService
import com.strimup.feature.search.domain.StreamerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface SearchModule {
    @Binds
    fun bindsStreamerRepository(impl: DefaultStreamerRepository): StreamerRepository

    companion object {
        @Provides
        fun providesStreamerApiService(retrofit: Retrofit): StreamerApiService {
            return retrofit.create(StreamerApiService::class.java)
        }
    }
}