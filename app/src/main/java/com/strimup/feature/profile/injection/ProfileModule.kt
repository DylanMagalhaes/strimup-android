package com.strimup.feature.profile.injection

import com.strimup.feature.profile.data.DefaultStreamerRepository
import com.strimup.feature.profile.domain.StreamerRepository
import com.strimup.feature.profile.domain.usecase.DefaultGetStreamerUseCase
import com.strimup.feature.profile.domain.usecase.GetStreamerUsecase
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
    fun bindsStreamerRepository(impl: DefaultStreamerRepository): StreamerRepository

    @Binds
    fun bindsStreamerUseCase(impl: DefaultGetStreamerUseCase): GetStreamerUsecase

    companion object {
        @Provides
        fun providesStreamerApiService(retrofit: Retrofit): com.strimup.feature.profile.data.StreamerApiService {
            return retrofit.create(com.strimup.feature.profile.data.StreamerApiService::class.java)
        }
    }


}