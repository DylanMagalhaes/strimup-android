package com.strimup.feature.search.injection

import com.strimup.feature.search.domain.usecase.DefaultGetStreamersUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SearchModule {

    @Binds
    fun bindsGetStreamersUseCase(impl: DefaultGetStreamersUseCase): com.strimup.feature.search.domain.usecase.GetStreamersUseCase
}