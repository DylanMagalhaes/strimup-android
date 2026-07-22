package com.strimup.feature.streamerprofile.injection

import com.strimup.feature.streamerprofile.data.StreamerApiService
import com.strimup.feature.streamerprofile.domain.usecase.DefaultGetStreamerUsecase
import com.strimup.feature.streamerprofile.domain.usecase.DefaultUpdateProfileUsecase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerOptionsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
interface ProfileModule {

    @Binds
    fun bindsStreamerRepository(impl: com.strimup.feature.streamerprofile.data.DefaultStreamerRepository): com.strimup.feature.streamerprofile.domain.StreamerRepository

    @Binds
    fun bindsStreamerUsecase(impl: DefaultGetStreamerUsecase): com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUsecase

    companion object {
        @Provides
        fun providesStreamerApiService(retrofit: Retrofit): StreamerApiService {
            return retrofit.create(StreamerApiService::class.java)
        }
    }


}