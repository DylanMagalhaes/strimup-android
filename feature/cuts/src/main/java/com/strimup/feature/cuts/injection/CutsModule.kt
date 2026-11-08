package com.strimup.feature.cuts.injection

import com.strimup.feature.cuts.data.CutsApiService
import com.strimup.feature.cuts.data.DefaultCutsRepository
import com.strimup.feature.cuts.data.local.DataStoreCutsDiscoveryRepository
import com.strimup.feature.cuts.domain.CutsDiscoveryRepository
import com.strimup.feature.cuts.domain.CutsRepository
import com.strimup.feature.cuts.domain.usecase.DefaultGetCachedCutUseCase
import com.strimup.feature.cuts.domain.usecase.DefaultGetCutsUseCase
import com.strimup.feature.cuts.domain.usecase.DefaultLikeCutUseCase
import com.strimup.feature.cuts.domain.usecase.DefaultMarkCutsOpenedUseCase
import com.strimup.feature.cuts.domain.usecase.DefaultObserveHasOpenedCutsUseCase
import com.strimup.feature.cuts.domain.usecase.GetCachedCutUseCase
import com.strimup.feature.cuts.domain.usecase.GetCutsUseCase
import com.strimup.feature.cuts.domain.usecase.LikeCutUseCase
import com.strimup.feature.cuts.domain.usecase.MarkCutsOpenedUseCase
import com.strimup.feature.cuts.domain.usecase.ObserveHasOpenedCutsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CutsNetworkModule {

    @Provides
    @Singleton
    fun providesCutsApiService(retrofit: Retrofit): CutsApiService {
        return retrofit.create(CutsApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface CutsDomainModule {

    @Binds
    @Singleton
    fun bindsCutsRepository(impl: DefaultCutsRepository): CutsRepository

    @Binds
    @Singleton
    fun bindsCutsDiscoveryRepository(impl: DataStoreCutsDiscoveryRepository): CutsDiscoveryRepository

    @Binds
    fun bindsGetCutsUseCase(impl: DefaultGetCutsUseCase): GetCutsUseCase

    @Binds
    fun bindsGetCachedCutUseCase(impl: DefaultGetCachedCutUseCase): GetCachedCutUseCase

    @Binds
    fun bindsLikeCutUseCase(impl: DefaultLikeCutUseCase): LikeCutUseCase

    @Binds
    fun bindsObserveHasOpenedCutsUseCase(impl: DefaultObserveHasOpenedCutsUseCase): ObserveHasOpenedCutsUseCase

    @Binds
    fun bindsMarkCutsOpenedUseCase(impl: DefaultMarkCutsOpenedUseCase): MarkCutsOpenedUseCase
}
