package com.strimup.feature.schedule.injection

import com.strimup.feature.schedule.data.DefaultScheduleRepository
import com.strimup.feature.schedule.data.ScheduleApiService
import com.strimup.feature.schedule.domain.ScheduleRepository
import com.strimup.feature.schedule.domain.usecase.CreateScheduleItemUseCase
import com.strimup.feature.schedule.domain.usecase.DefaultCreateScheduleItemUseCase
import com.strimup.feature.schedule.domain.usecase.DefaultDeleteScheduleItemUseCase
import com.strimup.feature.schedule.domain.usecase.DefaultGetScheduleUseCase
import com.strimup.feature.schedule.domain.usecase.DeleteScheduleItemUseCase
import com.strimup.feature.schedule.domain.usecase.GetScheduleUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface ScheduleModule {

    @Binds
    @Singleton
    fun bindScheduleRepository(impl: DefaultScheduleRepository): ScheduleRepository

    @Binds
    fun bindCreateScheduleItemUseCase(impl: DefaultCreateScheduleItemUseCase): CreateScheduleItemUseCase

    @Binds
    fun bindDeleteScheduleItemUseCase(impl: DefaultDeleteScheduleItemUseCase): DeleteScheduleItemUseCase

    @Binds
    fun bindGetScheduleUseCase(impl: DefaultGetScheduleUseCase): GetScheduleUseCase

    companion object {
        @Provides
        @Singleton
        fun providesScheduleApiService(retrofit: Retrofit): ScheduleApiService {
            return retrofit.create(ScheduleApiService::class.java)
        }
    }
}