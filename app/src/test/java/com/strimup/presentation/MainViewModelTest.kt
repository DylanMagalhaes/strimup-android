package com.strimup.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.entity.OAuthCallback
import com.strimup.feature.auth.domain.usecase.ApplyOAuthLoginUseCase
import com.strimup.feature.auth.domain.usecase.LogoutUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeUser = UserEntity(
        id = "1",
        userName = "inox",
        email = "inox@mail.com",
        role = UserRole.STREAMER,
        avatarUrl = "",
    )

    private fun buildViewModel(
        getUser: GetUserFlowUseCase = GetUserFlowUseCase { flowOf(fakeUser) },
        logout: LogoutUseCase = LogoutUseCase { Result.success(Unit) },
        applyOAuthLogin: ApplyOAuthLoginUseCase = ApplyOAuthLoginUseCase { _, _ -> Result.success(Unit) },
    ) = MainViewModel(
        getUser = getUser,
        logout = logout,
        applyOAuthLogin = applyOAuthLogin,
    )

    @Test
    fun `init should collect user flow and update state from loading to loaded user`() = runTest {
        // GIVEN
        val getUserUseCase = GetUserFlowUseCase { flowOf(fakeUser) }

        // WHEN
        val viewModel = buildViewModel(getUser = getUserUseCase)
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.loading).isFalse()
        assertThat(state.user).isEqualTo(fakeUser)
    }

    @Test
    fun `init when user flow emits null should update state with loading false and null user`() = runTest {
        // GIVEN
        val getUserUseCase = GetUserFlowUseCase { flowOf(null) }

        // WHEN
        val viewModel = buildViewModel(getUser = getUserUseCase)
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.loading).isFalse()
        assertThat(state.user).isNull()
    }

    @Test
    fun `init when user flow emits updated user should reflect latest user in state`() = runTest {
        // GIVEN
        val userFlow = MutableSharedFlow<UserEntity?>()
        val getUserUseCase = GetUserFlowUseCase { userFlow }

        val viewModel = buildViewModel(getUser = getUserUseCase)
        // Laisse le `collect { }` du init s'abonner à userFlow avant toute émission :
        // sinon un MutableSharedFlow sans replay perd la valeur émise.
        runCurrent()

        viewModel.state.test {

            val initialState = awaitItem()
            assertThat(initialState.loading).isTrue()
            assertThat(initialState.user).isNull()

            userFlow.emit(fakeUser)
            val updatedState = awaitItem()
            assertThat(updatedState.loading).isFalse()
            assertThat(updatedState.user).isEqualTo(fakeUser)

            val updatedUser = fakeUser.copy(userName = "inox_updated")
            userFlow.emit(updatedUser)
            val finalState = awaitItem()
            assertThat(finalState.loading).isFalse()
            assertThat(finalState.user).isEqualTo(updatedUser)
        }
    }

    @Test
    fun `onLogoutClick when logout succeeds should emit LoggedOut`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            logout = LogoutUseCase { Result.success(Unit) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(MainUiEvent.LoggedOut)
        }
    }

    @Test
    fun `onLogoutClick when logout fails should emit ShowSnackBar with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            logout = LogoutUseCase { Result.failure(Exception("peu importe")) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
    }

    @Test
    fun `onLogoutClick when logout fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            logout = LogoutUseCase { Result.failure(DomainException(DomainError.Network)) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
    }

    @Test
    fun `onOAuthCallback with LoggedIn should apply the session and emit OAuthLoggedIn`() = runTest {
        // GIVEN
        var capturedToken: String? = null
        var capturedRefreshToken: String? = null
        val viewModel = buildViewModel(
            applyOAuthLogin = ApplyOAuthLoginUseCase { token, refreshToken ->
                capturedToken = token
                capturedRefreshToken = refreshToken
                Result.success(Unit)
            },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.LoggedIn(token = "t", refreshToken = "r"))
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(MainUiEvent.OAuthLoggedIn)
        }
        assertThat(capturedToken).isEqualTo("t")
        assertThat(capturedRefreshToken).isEqualTo("r")
    }

    @Test
    fun `onOAuthCallback with LoggedIn when applying the session fails should emit ShowSnackBar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            applyOAuthLogin = ApplyOAuthLoginUseCase { _, _ -> Result.failure(Exception("peu importe")) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.LoggedIn(token = "t", refreshToken = "r"))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
    }

    @Test
    fun `onOAuthCallback with Linked should apply the session without a refresh token and emit a success snackbar`() =
        runTest {
            // GIVEN
            var capturedRefreshToken: String? = "not set"
            val viewModel = buildViewModel(
                applyOAuthLogin = ApplyOAuthLoginUseCase { _, refreshToken ->
                    capturedRefreshToken = refreshToken
                    Result.success(Unit)
                },
            )

            // WHEN & THEN
            viewModel.events.test {
                viewModel.onOAuthCallback(OAuthCallback.Linked(token = "t"))
                advanceUntilIdle()

                val event = awaitItem() as MainUiEvent.ShowSnackBar
                assertThat(event.textRes).isEqualTo(R.string.oauth_link_success)
            }
            assertThat(capturedRefreshToken).isNull()
        }

    @Test
    fun `onOAuthCallback with Onboarding should emit OAuthOnboardingRequired with the tmp token`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.Onboarding(tmp = "abc"))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.OAuthOnboardingRequired
            assertThat(event.tmp).isEqualTo("abc")
        }
    }

    @Test
    fun `onOAuthCallback with null should not emit anything`() = runTest {
        // GIVEN
        var applyOAuthLoginCalled = false
        val viewModel = buildViewModel(
            applyOAuthLogin = ApplyOAuthLoginUseCase { _, _ ->
                applyOAuthLoginCalled = true
                Result.success(Unit)
            },
        )

        // WHEN
        viewModel.onOAuthCallback(null)
        advanceUntilIdle()

        // THEN
        assertThat(applyOAuthLoginCalled).isFalse()
    }
}
