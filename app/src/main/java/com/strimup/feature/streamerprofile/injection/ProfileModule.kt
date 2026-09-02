package com.strimup.feature.streamerprofile.injection

import com.strimup.feature.streamerprofile.domain.usecase.DefaultGetStreamerUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ProfileModule {

    @Binds
    fun bindsStreamerUseCase(impl: DefaultGetStreamerUseCase): GetStreamerUseCase
}
