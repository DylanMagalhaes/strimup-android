package com.strimup.feature.auth.injection

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.DefaultAuthRepository
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.usecase.DefaultLoginUseCase
import com.strimup.feature.auth.domain.usecase.LoginUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    @Provides
    @Singleton
    fun providesAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface AuthDomainModule {

    @Binds
    @Singleton
    fun bindsAuthRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds
    fun bindsLoginUseCase(impl: DefaultLoginUseCase): LoginUseCase
}
