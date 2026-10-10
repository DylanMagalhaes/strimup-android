package com.strimup.feature.auth.injection

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.DefaultAuthRepository
import com.strimup.feature.auth.data.local.DefaultLocalSessionDataSource
import com.strimup.feature.auth.data.local.LocalSessionDataSource
import com.strimup.feature.auth.data.remote.AuthAuthenticator
import com.strimup.feature.auth.data.remote.AuthInterceptor
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.usecase.CompleteOAuthUseCase
import com.strimup.feature.auth.domain.usecase.DefaultCompleteOAuthUseCase
import com.strimup.feature.auth.domain.usecase.DefaultExchangeOAuthCodeUseCase
import com.strimup.feature.auth.domain.usecase.DefaultLoginUseCase
import com.strimup.feature.auth.domain.usecase.DefaultLogoutUseCase
import com.strimup.feature.auth.domain.usecase.DefaultRegisterUseCase
import com.strimup.feature.auth.domain.usecase.DefaultStartTwitchLoginUseCase
import com.strimup.feature.auth.domain.usecase.ExchangeOAuthCodeUseCase
import com.strimup.feature.auth.domain.usecase.LoginUseCase
import com.strimup.feature.auth.domain.usecase.LogoutUseCase
import com.strimup.feature.auth.domain.usecase.RegisterUseCase
import com.strimup.feature.auth.domain.usecase.StartTwitchLoginUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Authenticator
import okhttp3.Interceptor
import retrofit2.Retrofit
import javax.inject.Singleton

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
interface AuthHttpModule {

    @Binds
    @IntoSet
    fun bindsAuthInterceptor(impl: AuthInterceptor): Interceptor

    @Binds
    fun bindsAuthAuthenticator(impl: AuthAuthenticator): Authenticator
}

@Module
@InstallIn(SingletonComponent::class)
interface AuthDomainModule {

    @Binds
    @Singleton
    fun bindsAuthRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds
    fun bindsLocalSessionDataSource(impl: DefaultLocalSessionDataSource): LocalSessionDataSource

    @Binds
    fun bindsLoginUseCase(impl: DefaultLoginUseCase): LoginUseCase

    @Binds
    fun bindsRegisterUseCase(impl: DefaultRegisterUseCase): RegisterUseCase

    @Binds
    fun bindsLogoutUseCase(impl: DefaultLogoutUseCase): LogoutUseCase

    @Binds
    fun bindsStartTwitchLoginUseCase(impl: DefaultStartTwitchLoginUseCase): StartTwitchLoginUseCase

    @Binds
    fun bindsExchangeOAuthCodeUseCase(impl: DefaultExchangeOAuthCodeUseCase): ExchangeOAuthCodeUseCase

    @Binds
    fun bindsCompleteOAuthUseCase(impl: DefaultCompleteOAuthUseCase): CompleteOAuthUseCase
}
