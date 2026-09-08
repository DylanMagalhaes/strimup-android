package com.strimup.feature.auth.presentation.login

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.usecase.LoginUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `onEmailChange should update state emailInput`() = runTest {
        // GIVEN
        val viewModel = LoginViewModel(
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
        val errorMessage = "Identifiants invalides"
        val viewModel = LoginViewModel(
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
            assertThat(snackBarEvent.text).isEqualTo(errorMessage)
            
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.user).isNull()
        }
    }

    private companion object {
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