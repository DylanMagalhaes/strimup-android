package com.strimup.feature.push.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.push.domain.PushRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PushLifecycleUseCasesTest {

    private class FakePushRepository : PushRepository {
        val registeredTokens = mutableListOf<String?>()

        override suspend fun registerDevice(token: String?): Result<Unit> {
            registeredTokens += token
            return Result.success(Unit)
        }

        override suspend fun unregisterDevice(): Result<Unit> = Result.success(Unit)
        override suspend fun deleteLocalToken(): Result<Unit> = Result.success(Unit)
    }

    private val user = UserEntity(
        id = "1",
        userName = "Inox",
        email = "inox@test.com",
        role = UserRole.VIEWER,
        avatarUrl = null,
    )

    @Test
    fun `sync should register the device when a user is already logged in`() = runTest {
        val repository = FakePushRepository()
        val sync = DefaultSyncPushDeviceRegistrationUseCase(GetUserFlowUseCase { flowOf(user) }, repository)

        backgroundScope.launch { sync() }
        runCurrent()

        assertThat(repository.registeredTokens).containsExactly(null)
    }

    @Test
    fun `sync should register on each new login but not when logged out`() = runTest {
        val repository = FakePushRepository()
        val userFlow = MutableStateFlow<UserEntity?>(null)
        val sync = DefaultSyncPushDeviceRegistrationUseCase(GetUserFlowUseCase { userFlow }, repository)
        backgroundScope.launch { sync() }
        runCurrent()
        assertThat(repository.registeredTokens).isEmpty()

        userFlow.value = user
        runCurrent()
        userFlow.value = null
        runCurrent()
        userFlow.value = user.copy(id = "2")
        runCurrent()

        assertThat(repository.registeredTokens).hasSize(2)
    }

    @Test
    fun `sync should not register again when the same user profile changes`() = runTest {
        val repository = FakePushRepository()
        val userFlow = MutableStateFlow<UserEntity?>(user)
        val sync = DefaultSyncPushDeviceRegistrationUseCase(GetUserFlowUseCase { userFlow }, repository)
        backgroundScope.launch { sync() }
        runCurrent()

        userFlow.value = user.copy(userName = "Inox2")
        runCurrent()

        assertThat(repository.registeredTokens).hasSize(1)
    }

    @Test
    fun `refreshed token should be registered when logged in`() = runTest {
        val repository = FakePushRepository()
        val useCase = DefaultRegisterRefreshedPushTokenUseCase(GetUserFlowUseCase { flowOf(user) }, repository)

        useCase("new-token")

        assertThat(repository.registeredTokens).containsExactly("new-token")
    }

    @Test
    fun `refreshed token should be ignored when logged out`() = runTest {
        val repository = FakePushRepository()
        val useCase = DefaultRegisterRefreshedPushTokenUseCase(GetUserFlowUseCase { flowOf(null) }, repository)

        val result = useCase("new-token")

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.registeredTokens).isEmpty()
    }
}
