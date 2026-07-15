package com.strimup.feature.streamerdetail.injection

import com.strimup.feature.streamerdetail.data.DefaultStreamerRepository
import com.strimup.feature.streamerdetail.domain.StreamerRepository
import com.strimup.feature.streamerdetail.domain.usecase.DefaultGetStreamerUsecase
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUsecase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit


@Module
@InstallIn(ViewModelComponent::class)
interface DetailModule {

    @Binds
    fun bindsStreamerRepository(impl: DefaultStreamerRepository): StreamerRepository

    @Binds
    fun bindsStreamerUseCase(impl: DefaultGetStreamerUsecase): GetStreamerUsecase

    companion object {
        @Provides
        fun providesStreamerApiService(retrofit: Retrofit): com.strimup.feature.streamerdetail.data.StreamerApiService {
            return retrofit.create(com.strimup.feature.streamerdetail.data.StreamerApiService::class.java)
        }
    }


}