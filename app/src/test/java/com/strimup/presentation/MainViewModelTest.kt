package com.strimup.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCallback
import com.strimup.feature.auth.domain.entity.OAuthFailureReason
import com.strimup.feature.auth.domain.usecase.ExchangeOAuthCodeUseCase
import com.strimup.feature.notification.domain.usecase.WatchUnreadNotificationCountUseCase
import com.strimup.feature.push.domain.usecase.ObserveShouldAskNotificationPermissionUseCase
import com.strimup.feature.push.domain.usecase.SyncPushDeviceRegistrationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import com.strimup.core.ui.R as CoreUiR

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

    private var permissionHandledCount = 0

    private fun buildViewModel(
        getUser: GetUserFlowUseCase = GetUserFlowUseCase { flowOf(fakeUser) },
        exchangeOAuthCode: ExchangeOAuthCodeUseCase = ExchangeOAuthCodeUseCase { Result.success(fakeLoginResult) },
        watchUnreadNotificationCount: WatchUnreadNotificationCountUseCase = WatchUnreadNotificationCountUseCase {
            flowOf(0)
        },
        syncPushDeviceRegistration: SyncPushDeviceRegistrationUseCase = SyncPushDeviceRegistrationUseCase {},
        shouldAskPermission: ObserveShouldAskNotificationPermissionUseCase =
            ObserveShouldAskNotificationPermissionUseCase { flowOf(false) },
    ) = MainViewModel(
        getUser = getUser,
        exchangeOAuthCode = exchangeOAuthCode,
        watchUnreadNotificationCount = watchUnreadNotificationCount,
        syncPushDeviceRegistration = syncPushDeviceRegistration,
        observeShouldAskNotificationPermission = shouldAskPermission,
        markNotificationPermissionAsked = { permissionHandledCount++ },
    )

    private val fakeLoginResult = LoginResultEntity(
        message = "Connexion réussie",
        token = "t",
        user = fakeUser,
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
    fun `onOAuthCallback with LoggedIn should exchange the code and emit OAuthLoggedIn`() = runTest {
        // GIVEN
        var capturedCode: String? = null
        val viewModel = buildViewModel(
            exchangeOAuthCode = ExchangeOAuthCodeUseCase { code ->
                capturedCode = code
                Result.success(fakeLoginResult)
            },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.LoggedIn(code = "one-time-code"))
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(MainUiEvent.OAuthLoggedIn)
        }
        assertThat(capturedCode).isEqualTo("one-time-code")
    }

    @Test
    fun `onOAuthCallback with LoggedIn when the code is rejected should emit the OAuth failure snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            exchangeOAuthCode = ExchangeOAuthCodeUseCase { Result.failure(DomainException(DomainError.Server(400))) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.LoggedIn(code = "expired"))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(CoreUiR.string.oauth_error_failed)
        }
    }

    @Test
    fun `onOAuthCallback with LoggedIn when the server is down should emit the server error snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            exchangeOAuthCode = ExchangeOAuthCodeUseCase { Result.failure(DomainException(DomainError.Server(503))) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.LoggedIn(code = "c"))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(CoreUiR.string.error_server)
        }
    }

    @Test
    fun `onOAuthCallback with LoggedIn when offline should emit the network error snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            exchangeOAuthCode = ExchangeOAuthCodeUseCase { Result.failure(DomainException(DomainError.Network)) },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.LoggedIn(code = "c"))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(CoreUiR.string.error_network)
        }
    }

    @Test
    fun `onOAuthCallback with Linked should exchange the code and emit a success snackbar`() = runTest {
        // GIVEN
        var capturedCode: String? = null
        val viewModel = buildViewModel(
            exchangeOAuthCode = ExchangeOAuthCodeUseCase { code ->
                capturedCode = code
                Result.success(fakeLoginResult)
            },
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.Linked(code = "link-code"))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.oauth_link_success)
        }
        assertThat(capturedCode).isEqualTo("link-code")
    }

    @Test
    fun `onOAuthCallback with Failed access_denied should emit the cancelled snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.Failed(OAuthFailureReason.ACCESS_DENIED))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.oauth_error_access_denied)
        }
    }

    @Test
    fun `onOAuthCallback with Failed invalid_state should emit the expired snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.Failed(OAuthFailureReason.INVALID_STATE))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.oauth_error_expired)
        }
    }

    @Test
    fun `onOAuthCallback with Failed server_error should emit the generic OAuth failure snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onOAuthCallback(OAuthCallback.Failed(OAuthFailureReason.SERVER_ERROR))
            advanceUntilIdle()

            val event = awaitItem() as MainUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(CoreUiR.string.oauth_error_failed)
        }
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
    fun `onOAuthCallback with null should not exchange anything`() = runTest {
        // GIVEN
        var exchangeCalled = false
        val viewModel = buildViewModel(
            exchangeOAuthCode = ExchangeOAuthCodeUseCase {
                exchangeCalled = true
                Result.success(fakeLoginResult)
            },
        )

        // WHEN
        viewModel.onOAuthCallback(null)
        advanceUntilIdle()

        // THEN
        assertThat(exchangeCalled).isFalse()
    }

    @Test
    fun `unreadNotificationCount should expose the watched count while collected`() = runTest {
        val viewModel = buildViewModel(watchUnreadNotificationCount = { flowOf(7) })

        viewModel.unreadNotificationCount.test {
            assertThat(awaitItem()).isEqualTo(0)
            assertThat(awaitItem()).isEqualTo(7)
        }
    }

    @Test
    fun `unreadNotificationCount should not watch anything until collected`() = runTest {
        var watchStarted = false
        val viewModel = buildViewModel(
            watchUnreadNotificationCount = {
                flow {
                    watchStarted = true
                    emit(1)
                }
            },
        )
        advanceUntilIdle()

        assertThat(watchStarted).isFalse()
        assertThat(viewModel.unreadNotificationCount.value).isEqualTo(0)
    }

    @Test
    fun `init should start the push device registration sync`() = runTest {
        var syncStarted = false
        buildViewModel(syncPushDeviceRegistration = { syncStarted = true })

        advanceUntilIdle()

        assertThat(syncStarted).isTrue()
    }

    @Test
    fun `onOpenNotificationsRequested should open the notifications when logged in`() = runTest {
        val viewModel = buildViewModel(getUser = GetUserFlowUseCase { flowOf(fakeUser) })

        viewModel.events.test {
            viewModel.onOpenNotificationsRequested()
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(MainUiEvent.OpenNotifications)
        }
    }

    @Test
    fun `onOpenNotificationsRequested should do nothing when logged out`() = runTest {
        val viewModel = buildViewModel(getUser = GetUserFlowUseCase { flowOf(null) })

        viewModel.events.test {
            viewModel.onOpenNotificationsRequested()
            advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `shouldAskNotificationPermission should expose the use case value`() = runTest {
        val viewModel = buildViewModel(shouldAskPermission = { flowOf(true) })
        advanceUntilIdle()

        assertThat(viewModel.shouldAskNotificationPermission.value).isTrue()
    }

    @Test
    fun `onNotificationPermissionHandled should remember that the permission was asked`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onNotificationPermissionHandled()
        advanceUntilIdle()

        assertThat(permissionHandledCount).isEqualTo(1)
    }
}
