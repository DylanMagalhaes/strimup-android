package com.strimup.feature.auth.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.auth.domain.AuthRepository
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCredentials
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import com.strimup.feature.push.domain.usecase.UnregisterPushDeviceUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultLogoutUseCaseTest {

    private class FakeAuthRepository(private val calls: MutableList<String>) : AuthRepository {
        override suspend fun login(email: String, password: String): Result<LoginResultEntity> = error("unused")
        override suspend fun register(credentials: RegisterCredentials): Result<LoginResultEntity> = error("unused")
        override suspend fun createTwitchLoginUrl(): Result<String> = error("unused")
        override suspend fun exchangeOAuthCode(code: String): Result<LoginResultEntity> = error("unused")
        override suspend fun completeOAuth(credentials: OAuthCredentials): Result<LoginResultEntity> = error("unused")

        override suspend fun logout(): Result<Unit> {
            calls += "logout"
            return Result.success(Unit)
        }
    }

    @Test
    fun `logout should unregister the push device before clearing the session`() = runTest {
        val calls = mutableListOf<String>()
        val useCase = DefaultLogoutUseCase(
            repository = FakeAuthRepository(calls),
            unregisterPushDevice = {
                calls += "unregister"
                Result.success(Unit)
            },
        )

        useCase()

        assertThat(calls).containsExactly("unregister", "logout").inOrder()
    }

    @Test
    fun `push unregistration failure should not block the logout`() = runTest {
        val calls = mutableListOf<String>()
        val useCase = DefaultLogoutUseCase(
            repository = FakeAuthRepository(calls),
            unregisterPushDevice = UnregisterPushDeviceUseCase { Result.failure(IllegalStateException()) },
        )

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        assertThat(calls).containsExactly("logout")
    }
}
