package com.strimup.feature.home.injection

import com.strimup.feature.home.data.DefaultStreamerRepository
import com.strimup.feature.home.data.StreamerApiService
import com.strimup.feature.home.domain.StreamerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
interface HomeModule {
    @Binds
    fun bindsStreamerRepository(impl: DefaultStreamerRepository): StreamerRepository

    companion object {
        @Provides
        fun providesStreamerApiService(retrofit: Retrofit): StreamerApiService {
            return retrofit.create(StreamerApiService::class.java)
        }
    }
}