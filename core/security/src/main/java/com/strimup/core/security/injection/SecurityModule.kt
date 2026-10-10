package com.strimup.core.security.injection

import com.strimup.core.security.KeystoreSecretCipher
import com.strimup.core.security.SecretCipher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SecurityModule {

    @Binds
    fun bindsSecretCipher(impl: KeystoreSecretCipher): SecretCipher
}
