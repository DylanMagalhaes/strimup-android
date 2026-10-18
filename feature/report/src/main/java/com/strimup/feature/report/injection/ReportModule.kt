package com.strimup.feature.report.injection

import com.strimup.feature.report.data.DefaultReportRepository
import com.strimup.feature.report.data.ReportApiService
import com.strimup.feature.report.domain.ReportRepository
import com.strimup.feature.report.domain.usecase.DefaultReportStreamerUseCase
import com.strimup.feature.report.domain.usecase.ReportStreamerUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportNetworkModule {

    @Provides
    @Singleton
    fun providesReportApiService(retrofit: Retrofit): ReportApiService {
        return retrofit.create(ReportApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface ReportDomainModule {

    @Binds
    @Singleton
    fun bindsReportRepository(impl: DefaultReportRepository): ReportRepository

    @Binds
    fun bindsReportStreamerUseCase(impl: DefaultReportStreamerUseCase): ReportStreamerUseCase
}
