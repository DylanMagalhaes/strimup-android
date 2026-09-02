package com.strimup.core.streamer.injection

import com.strimup.core.streamer.data.StreamerApiService
import com.strimup.core.streamer.data.repository.DefaultStreamerRepository
import com.strimup.core.streamer.domain.repository.StreamerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface StreamerCoreModule {

    @Binds
    @Singleton
    fun bindsStreamerRepository(impl: DefaultStreamerRepository): StreamerRepository

    companion object {
        @Provides
        @Singleton
        fun providesStreamerApiService(retrofit: Retrofit): StreamerApiService {
            return retrofit.create(StreamerApiService::class.java)
        }
    }
}
