package com.strimup.feature.push.injection

import com.strimup.feature.push.data.DefaultPushRepository
import com.strimup.feature.push.data.DeviceApiService
import com.strimup.feature.push.data.client.FirebasePushMessagingClient
import com.strimup.feature.push.data.client.PushMessagingClient
import com.strimup.feature.push.domain.PushRepository
import com.strimup.feature.push.domain.usecase.DefaultDeletePushTokenUseCase
import com.strimup.feature.push.domain.usecase.DefaultRegisterRefreshedPushTokenUseCase
import com.strimup.feature.push.domain.usecase.DefaultSyncPushDeviceRegistrationUseCase
import com.strimup.feature.push.domain.usecase.DefaultUnregisterPushDeviceUseCase
import com.strimup.feature.push.domain.usecase.DeletePushTokenUseCase
import com.strimup.feature.push.domain.usecase.RegisterRefreshedPushTokenUseCase
import com.strimup.feature.push.domain.usecase.SyncPushDeviceRegistrationUseCase
import com.strimup.feature.push.domain.usecase.UnregisterPushDeviceUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PushNetworkModule {

    @Provides
    @Singleton
    fun providesDeviceApiService(retrofit: Retrofit): DeviceApiService {
        return retrofit.create(DeviceApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface PushDomainModule {

    @Binds
    @Singleton
    fun bindsPushRepository(impl: DefaultPushRepository): PushRepository

    @Binds
    fun bindsPushMessagingClient(impl: FirebasePushMessagingClient): PushMessagingClient

    @Binds
    fun bindsSyncPushDeviceRegistrationUseCase(
        impl: DefaultSyncPushDeviceRegistrationUseCase,
    ): SyncPushDeviceRegistrationUseCase

    @Binds
    fun bindsRegisterRefreshedPushTokenUseCase(
        impl: DefaultRegisterRefreshedPushTokenUseCase,
    ): RegisterRefreshedPushTokenUseCase

    @Binds
    fun bindsDeletePushTokenUseCase(impl: DefaultDeletePushTokenUseCase): DeletePushTokenUseCase

    @Binds
    fun bindsUnregisterPushDeviceUseCase(impl: DefaultUnregisterPushDeviceUseCase): UnregisterPushDeviceUseCase
}
