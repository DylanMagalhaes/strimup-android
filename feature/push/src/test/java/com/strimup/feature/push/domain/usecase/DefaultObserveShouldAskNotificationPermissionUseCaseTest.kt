package com.strimup.feature.push.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.push.domain.NotificationPermissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultObserveShouldAskNotificationPermissionUseCaseTest {

    private class FakeNotificationPermissionRepository : NotificationPermissionRepository {
        override val hasAskedPermission = MutableStateFlow(false)

        override suspend fun markPermissionAsked() {
            hasAskedPermission.value = true
        }
    }

    private val user = UserEntity(
        id = "1",
        userName = "Inox",
        email = "inox@test.com",
        role = UserRole.VIEWER,
        avatarUrl = null,
    )

    @Test
    fun `should not ask while logged out`() = runTest {
        val useCase = DefaultObserveShouldAskNotificationPermissionUseCase(
            GetUserFlowUseCase { MutableStateFlow(null) },
            FakeNotificationPermissionRepository(),
        )

        useCase().test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `should ask once a user logs in, then stop once handled`() = runTest {
        val userFlow = MutableStateFlow<UserEntity?>(null)
        val repository = FakeNotificationPermissionRepository()
        val useCase = DefaultObserveShouldAskNotificationPermissionUseCase(GetUserFlowUseCase { userFlow }, repository)

        useCase().test {
            assertThat(awaitItem()).isFalse()

            userFlow.value = user
            assertThat(awaitItem()).isTrue()

            DefaultMarkNotificationPermissionAskedUseCase(repository).invoke()
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `should never ask again after a new login once handled`() = runTest {
        val repository = FakeNotificationPermissionRepository().apply { hasAskedPermission.value = true }
        val userFlow = MutableStateFlow<UserEntity?>(user)
        val useCase = DefaultObserveShouldAskNotificationPermissionUseCase(GetUserFlowUseCase { userFlow }, repository)

        useCase().test {
            assertThat(awaitItem()).isFalse()
            userFlow.value = null
            userFlow.value = user.copy(id = "2")
            expectNoEvents()
        }
    }
}
