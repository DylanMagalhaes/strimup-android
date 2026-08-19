package com.strimup.feature.streamerprofile.injection

import com.strimup.feature.streamerprofile.domain.usecase.DefaultGetStreamerUsecase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUsecase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ProfileModule {

    @Binds
    fun bindsStreamerUsecase(impl: DefaultGetStreamerUsecase): GetStreamerUsecase
}
