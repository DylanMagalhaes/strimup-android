package com.strimup.feature.home.injection

import com.strimup.feature.home.data.FakeStreamerRepository
import com.strimup.feature.home.domain.StreamerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface HomeModule {

    @Binds
    fun bindsStreamerRepository(impl: FakeStreamerRepository): StreamerRepository
}