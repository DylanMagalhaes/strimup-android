package com.strimup.feature.search.injection

import com.strimup.feature.search.data.DefaultRecentStreamerRepository
import com.strimup.feature.search.domain.RecentStreamerRepository
import com.strimup.feature.search.domain.usecase.DefaultGetLiveStreamersUseCase
import com.strimup.feature.search.domain.usecase.DefaultGetStreamersUseCase
import com.strimup.feature.search.domain.usecase.DefaultObserveRecentStreamersUseCase
import com.strimup.feature.search.domain.usecase.DefaultRemoveRecentStreamerUseCase
import com.strimup.feature.search.domain.usecase.DefaultSaveRecentStreamerUseCase
import com.strimup.feature.search.domain.usecase.GetLiveStreamersUseCase
import com.strimup.feature.search.domain.usecase.GetStreamersUseCase
import com.strimup.feature.search.domain.usecase.ObserveRecentStreamersUseCase
import com.strimup.feature.search.domain.usecase.RemoveRecentStreamerUseCase
import com.strimup.feature.search.domain.usecase.SaveRecentStreamerUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SearchModule {

    @Binds
    fun bindsGetStreamersUseCase(impl: DefaultGetStreamersUseCase): GetStreamersUseCase

    @Binds
    fun bindsGetLiveStreamersUseCase(impl: DefaultGetLiveStreamersUseCase): GetLiveStreamersUseCase

    @Binds
    fun bindsRecentStreamerRepository(impl: DefaultRecentStreamerRepository): RecentStreamerRepository

    @Binds
    fun bindsObserveRecentStreamersUseCase(impl: DefaultObserveRecentStreamersUseCase): ObserveRecentStreamersUseCase

    @Binds
    fun bindsSaveRecentStreamerUseCase(impl: DefaultSaveRecentStreamerUseCase): SaveRecentStreamerUseCase

    @Binds
    fun bindsRemoveRecentStreamerUseCase(impl: DefaultRemoveRecentStreamerUseCase): RemoveRecentStreamerUseCase
}
