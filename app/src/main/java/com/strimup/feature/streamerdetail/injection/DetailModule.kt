package com.strimup.feature.streamerdetail.injection

import com.strimup.feature.streamerdetail.domain.usecase.DefaultGetStreamerUsecase
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUsecase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
interface DetailModule {

    @Binds
    fun bindsStreamerUseCase(impl: DefaultGetStreamerUsecase): GetStreamerUsecase
}
