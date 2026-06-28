package com.strimup.feature.auth.injection

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.DefaultAuthRepository
import com.strimup.feature.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
interface AuthModule {
    @Binds
    fun bindsSessionRepository(impl: DefaultAuthRepository): AuthRepository

    companion object {
        @Provides
        fun providesAuthApiService(retrofit: Retrofit): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }
    }
}