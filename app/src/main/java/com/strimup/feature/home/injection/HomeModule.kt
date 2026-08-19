package com.strimup.feature.home.injection

import com.strimup.feature.home.domain.usecase.GetStreamersUsecase
import com.strimup.feature.home.domain.usecase.GetStreamersWithoutFavoriteUsecase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface HomeModule {
    @Binds
    fun bindsStreamerUsecase(impl: GetStreamersWithoutFavoriteUsecase): GetStreamersUsecase
}
