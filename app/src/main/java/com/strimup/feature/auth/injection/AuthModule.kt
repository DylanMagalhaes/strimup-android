package com.strimup.feature.auth.injection

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.DefaultAuthRepository
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.usecase.LoginUsecase
import com.strimup.feature.auth.domain.usecase.DefaultLoginUsecase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    @Provides
    @Singleton // 👈 On dit à Hilt de garder une seule instance globale
    fun providesAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
interface AuthDomainModule {

    @Binds
    fun bindsSessionRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds
    fun bindsLoginUsecase(impl: DefaultLoginUsecase): LoginUsecase
}