package com.strimup.core.user.injection

import com.strimup.core.user.data.DefaultUserRepository
import com.strimup.core.user.domain.UserRepository
import com.strimup.core.user.domain.usecase.DefaultGetUserFlowUseCase
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UserModule {

    @Binds
    @Singleton
    fun bindsUserRepository(impl: DefaultUserRepository): UserRepository

    @Binds
    fun bindsGetUserFlowUseCase(impl: DefaultGetUserFlowUseCase): GetUserFlowUseCase
}
