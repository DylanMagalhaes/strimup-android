package com.strimup.feature.filter.injection

import com.strimup.feature.filter.data.DefaultFilterOptionsRepository
import com.strimup.feature.filter.data.DefaultFilterRepository
import com.strimup.feature.filter.data.FilterApiService
import com.strimup.feature.filter.domain.FilterOptionRepository
import com.strimup.feature.filter.domain.FilterRepository
import com.strimup.feature.filter.domain.usecase.CreateFilterUseCase
import com.strimup.feature.filter.domain.usecase.DefaultCreateFilterUseCase
import com.strimup.feature.filter.domain.usecase.DefaultDeleteFilterUseCase
import com.strimup.feature.filter.domain.usecase.DefaultGetFilterByIdUseCase
import com.strimup.feature.filter.domain.usecase.DefaultGetFilterOptionsUseCase
import com.strimup.feature.filter.domain.usecase.DefaultGetFiltersUseCase
import com.strimup.feature.filter.domain.usecase.DefaultGetStreamersByFilterUseCase
import com.strimup.feature.filter.domain.usecase.DeleteFilterUseCase
import com.strimup.feature.filter.domain.usecase.GetFilterByIdUseCase
import com.strimup.feature.filter.domain.usecase.GetFilterOptionsUseCase
import com.strimup.feature.filter.domain.usecase.GetFiltersUseCase
import com.strimup.feature.filter.domain.usecase.GetStreamersByFilterUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface FilterModule {

    @Binds
    @Singleton
    fun bindFilterRepository(impl: DefaultFilterRepository): FilterRepository

    @Binds
    @Singleton
    fun bindFilterOptionRepository(impl: DefaultFilterOptionsRepository): FilterOptionRepository

    @Binds
    fun bindCreateFilterUseCase(impl: DefaultCreateFilterUseCase): CreateFilterUseCase

    @Binds
    fun bindDeleteFilterUseCase(impl: DefaultDeleteFilterUseCase): DeleteFilterUseCase

    @Binds
    fun bindGetFilterByIdUseCase(impl: DefaultGetFilterByIdUseCase): GetFilterByIdUseCase

    @Binds
    fun bindGetFilterOptionsUseCase(impl: DefaultGetFilterOptionsUseCase): GetFilterOptionsUseCase

    @Binds
    fun bindGetFiltersUseCase(impl: DefaultGetFiltersUseCase): GetFiltersUseCase

    @Binds
    fun bindGetStreamersByFilterUseCase(impl: DefaultGetStreamersByFilterUseCase): GetStreamersByFilterUseCase

    companion object {
        @Provides
        @Singleton
        fun providesFilterApiService(retrofit: Retrofit): FilterApiService {
            return retrofit.create(FilterApiService::class.java)
        }
    }
}
