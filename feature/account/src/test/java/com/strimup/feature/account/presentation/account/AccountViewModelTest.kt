package com.strimup.feature.account.presentation.account

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.R
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.usecase.LogoutUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeUser = UserEntity(
        id = "1",
        userName = "Inox",
        email = "inox@strimup.com",
        role = UserRole.VIEWER,
        avatarUrl = null,
    )

    private fun buildViewModel(
        getUser: GetUserFlowUseCase = GetUserFlowUseCase { flowOf(fakeUser) },
        logout: LogoutUseCase = LogoutUseCase { Result.success(Unit) },
    ) = AccountViewModel(
        getUser = getUser,
        logout = logout,
    )

    @Test
    fun `init should expose the current user`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.user).isEqualTo(fakeUser)
    }

    @Test
    fun `onLogoutClick when logout succeeds should emit LoggedOut`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(AccountUiEvent.LoggedOut)
        }
    }

    @Test
    fun `onLogoutClick when logout fails should show the mapped error and unlock the screen`() = runTest {
        val viewModel = buildViewModel(
            logout = { Result.failure(DomainException(DomainError.Network)) },
        )
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            val event = awaitItem() as AccountUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
        assertThat(viewModel.state.value.isLoggingOut).isFalse()
    }

    @Test
    fun `onLogoutClick while a logout is running should be ignored`() = runTest {
        val pendingResult = CompletableDeferred<Result<Unit>>()
        var callCount = 0
        val viewModel = buildViewModel(
            logout = {
                callCount++
                pendingResult.await()
            },
        )
        advanceUntilIdle()

        viewModel.onLogoutClick()
        advanceUntilIdle()
        viewModel.onLogoutClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isLoggingOut).isTrue()
        assertThat(callCount).isEqualTo(1)

        pendingResult.complete(Result.success(Unit))
        advanceUntilIdle()
    }
}
