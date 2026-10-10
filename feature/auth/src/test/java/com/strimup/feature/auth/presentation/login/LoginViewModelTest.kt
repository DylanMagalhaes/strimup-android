package com.strimup.feature.auth.presentation.login

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.R
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.usecase.LoginUseCase
import com.strimup.feature.auth.domain.usecase.StartTwitchLoginUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import com.strimup.core.ui.R as CoreUiR

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `onEmailChange should update state emailInput`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = LoginUseCase { _, _ -> Result.success(fakeLoginResult) }
        )

        // WHEN
        viewModel.onEmailChange("test@strimup.com")

        // THEN
        assertThat(viewModel.state.value.emailInput).isEqualTo("test@strimup.com")
    }

    @Test
    fun `onPasswordChange should update state passwordInput`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = LoginUseCase { _, _ -> Result.success(fakeLoginResult) }
        )

        // WHEN
        viewModel.onPasswordChange("password123")

        // THEN
        assertThat(viewModel.state.value.passwordInput).isEqualTo("password123")
    }

    @Test
    fun `onLoginButtonClick with blank email should not execute login`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = LoginUseCase { _, _ ->
                useCaseCalled = true
                Result.success(fakeLoginResult)
            }
        )

        viewModel.onEmailChange("  ")
        viewModel.onPasswordChange("password123")

        // WHEN
        viewModel.onLoginButtonClick()

        // THEN
        assertThat(useCaseCalled).isFalse()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `onLoginButtonClick with blank password should not execute login`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = { _, _ ->
                useCaseCalled = true
                Result.success(fakeLoginResult)
            }
        )

        viewModel.onEmailChange("test@strimup.com")
        viewModel.onPasswordChange("")

        // WHEN
        viewModel.onLoginButtonClick()

        // THEN
        assertThat(useCaseCalled).isFalse()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `onLoginButtonClick when login succeeds should update state and emit ShowHomeUi event`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = { _, _ -> Result.success(fakeLoginResult) }
        )

        viewModel.onEmailChange("test@strimup.com")
        viewModel.onPasswordChange("password123")

        // WHEN & THEN
        viewModel.event.test {
            viewModel.onLoginButtonClick()

            val event = awaitItem()
            assertThat(event).isInstanceOf(LoginUiEvent.ShowHomeUi::class.java)

            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.user).isEqualTo(fakeLoginResult.user)
        }
    }

    @Test
    fun `onLoginButtonClick when login fails should reset isLoading and emit ShowSnackBar event`() = runTest {
        // GIVEN
        val errorMessage = "Erreur technique brute"
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = LoginUseCase { _, _ -> Result.failure(Exception(errorMessage)) }
        )

        viewModel.onEmailChange("test@strimup.com")
        viewModel.onPasswordChange("wrongpassword")

        // WHEN & THEN
        viewModel.event.test {
            viewModel.onLoginButtonClick()

            val event = awaitItem()
            assertThat(event).isInstanceOf(LoginUiEvent.ShowSnackBar::class.java)

            val snackBarEvent = event as LoginUiEvent.ShowSnackBar
            assertThat(snackBarEvent.message).isEqualTo(UiText.Resource(CoreUiR.string.error_unknown))
            
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.user).isNull()
        }
    }

    @Test
    fun `onTwitchLoginClick when the URL is ready should emit OpenCustomTab with it`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
            login = { _, _ -> Result.success(fakeLoginResult) },
            startTwitchLogin = { Result.success("https://api/auth/twitch/login?code_challenge=abc") },
        )

        // WHEN & THEN
        viewModel.event.test {
            viewModel.onTwitchLoginClick()

            val event = awaitItem() as LoginUiEvent.OpenCustomTab
            assertThat(event.url).isEqualTo("https://api/auth/twitch/login?code_challenge=abc")
        }
    }

    @Test
    fun `onTwitchLoginClick when preparing the flow fails should emit the OAuth failure snackbar`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
            login = { _, _ -> Result.success(fakeLoginResult) },
            startTwitchLogin = { Result.failure(Exception("keystore indisponible")) },
        )

        // WHEN & THEN
        viewModel.event.test {
            viewModel.onTwitchLoginClick()

            val event = awaitItem() as LoginUiEvent.ShowSnackBar
            assertThat(event.message).isEqualTo(UiText.Resource(CoreUiR.string.oauth_error_failed))
        }
    }

    @Test
    fun `onForgotPasswordClick should open the forgot password page`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
            login = { _, _ -> Result.success(fakeLoginResult) },
            startTwitchLogin = { Result.success("") },
        )

        // WHEN & THEN
        viewModel.event.test {
            viewModel.onForgotPasswordClick()

            val event = awaitItem() as LoginUiEvent.OpenCustomTab
            assertThat(event.url).isEqualTo("https://www.strimup.com/forgot-password")
        }
    }

    @Test
    fun `wrong credentials should show the server message instead of a session expiry`() = runTest {
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = { _, _ ->
                Result.failure(DomainException(DomainError.Server(401, "E-mail ou mot de passe incorrect")))
            },
        )
        viewModel.onEmailChange("test@strimup.com")
        viewModel.onPasswordChange("wrong")

        viewModel.event.test {
            viewModel.onLoginButtonClick()

            val event = awaitItem() as LoginUiEvent.ShowSnackBar
            assertThat(event.message).isEqualTo(UiText.Dynamic("E-mail ou mot de passe incorrect"))
        }
    }

    @Test
    fun `wrong credentials without server message should show the default message`() = runTest {
        val viewModel = LoginViewModel(
            startTwitchLogin = noopStartTwitchLogin,
            login = { _, _ -> Result.failure(DomainException(DomainError.Server(401))) },
        )
        viewModel.onEmailChange("test@strimup.com")
        viewModel.onPasswordChange("wrong")

        viewModel.event.test {
            viewModel.onLoginButtonClick()

            val event = awaitItem() as LoginUiEvent.ShowSnackBar
            assertThat(event.message).isEqualTo(UiText.Resource(R.string.error_invalid_credentials))
        }
    }

    private companion object {
        val noopStartTwitchLogin = StartTwitchLoginUseCase { Result.success("https://twitch") }

        val fakeLoginResult = LoginResultEntity(
            message = "Success",
            token = "fake_jwt_token",
            user = UserEntity(
                id = "1",
                userName = "Inox",
                email = "test@strimup.com",
                role = UserRole.STREAMER,
                avatarUrl = null
            )
        )
    }
}
