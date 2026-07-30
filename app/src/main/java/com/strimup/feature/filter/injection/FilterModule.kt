package com.strimup.feature.filter.injection

import com.strimup.feature.filter.data.FilterApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
interface FilterModule {

    companion object{
        @Provides
        fun providesFilterApiService(retrofit: Retrofit): FilterApiService {
            return retrofit.create(FilterApiService::class.java)
        }
    }
}