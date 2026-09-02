package com.strimup.feature.streamerdetail.injection

import com.strimup.feature.streamerdetail.domain.usecase.DefaultGetStreamerUseCase
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DetailModule {

    @Binds
    fun bindsStreamerUseCase(impl: DefaultGetStreamerUseCase): GetStreamerUseCase
}
