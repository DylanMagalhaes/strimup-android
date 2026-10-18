package com.strimup.feature.account.injection

import com.strimup.feature.account.data.AccountApiService
import com.strimup.feature.account.data.DefaultAccountRepository
import com.strimup.feature.account.domain.AccountRepository
import com.strimup.feature.account.domain.usecase.DefaultDeleteAccountUseCase
import com.strimup.feature.account.domain.usecase.DefaultGetAccountDeletionPolicyUseCase
import com.strimup.feature.account.domain.usecase.DeleteAccountUseCase
import com.strimup.feature.account.domain.usecase.GetAccountDeletionPolicyUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountNetworkModule {

    @Provides
    @Singleton
    fun providesAccountApiService(retrofit: Retrofit): AccountApiService {
        return retrofit.create(AccountApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface AccountDomainModule {

    @Binds
    @Singleton
    fun bindsAccountRepository(impl: DefaultAccountRepository): AccountRepository

    @Binds
    fun bindsGetAccountDeletionPolicyUseCase(
        impl: DefaultGetAccountDeletionPolicyUseCase,
    ): GetAccountDeletionPolicyUseCase

    @Binds
    fun bindsDeleteAccountUseCase(impl: DefaultDeleteAccountUseCase): DeleteAccountUseCase
}
