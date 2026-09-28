package com.strimup.feature.auth.presentation.register

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.usecase.RegisterUseCase
import com.strimup.feature.auth.domain.usecase.StartTwitchLoginUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        register: RegisterUseCase = RegisterUseCase { _ -> Result.success(fakeRegisterResult) },
        startTwitchLogin: StartTwitchLoginUseCase = StartTwitchLoginUseCase { Result.success("https://twitch") },
    ) = RegisterViewModel(register = register, startTwitchLogin = startTwitchLogin)

    private fun fillValidForm(viewModel: RegisterViewModel) {
        viewModel.onPseudoChange("Inox")
        viewModel.onEmailChange("inox@test.com")
        viewModel.onBirthDateChange("1995-05-05")
        viewModel.onGenderChange(Gender.MALE)
        viewModel.onRoleChange(UserRole.VIEWER)
        viewModel.onPasswordChange("Password123!")
        viewModel.onConfirmPasswordChange("Password123!")
    }

    @Test
    fun `onPseudoChange should update state pseudoInput`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        viewModel.onPseudoChange("Inox")

        // THEN
        assertThat(viewModel.state.value.pseudoInput).isEqualTo("Inox")
    }

    @Test
    fun `onGenderChange should update state genderInput`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        viewModel.onGenderChange(Gender.NON_BINARY)

        // THEN
        assertThat(viewModel.state.value.genderInput).isEqualTo(Gender.NON_BINARY)
    }

    @Test
    fun `onRoleChange should update state roleInput`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        viewModel.onRoleChange(UserRole.STREAMER)

        // THEN
        assertThat(viewModel.state.value.roleInput).isEqualTo(UserRole.STREAMER)
    }

    @Test
    fun `isSubmitEnabled should be false when passwords do not match`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        // WHEN
        viewModel.onConfirmPasswordChange("different")

        // THEN
        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()
    }

    @Test
    fun `onRegisterButtonClick with incomplete form should not execute register`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                useCaseCalled = true
                Result.success(fakeRegisterResult)
            }
        )
        viewModel.onPseudoChange("Inox")

        // WHEN
        viewModel.onRegisterButtonClick()

        // THEN
        assertThat(useCaseCalled).isFalse()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `onRegisterButtonClick when register succeeds should reset isLoading and emit ShowHomeUi event`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onRegisterButtonClick()

            val event = awaitItem()
            assertThat(event).isInstanceOf(RegisterUiEvent.ShowHomeUi::class.java)
            assertThat(viewModel.state.value.isLoading).isFalse()
        }
    }

    @Test
    fun `onRegisterButtonClick when register fails should emit ShowSnackBar with the mapped error`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ -> Result.failure(Exception("peu importe")) }
        )
        fillValidForm(viewModel)

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onRegisterButtonClick()

            val event = awaitItem() as RegisterUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `onRegisterButtonClick when register fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                Result.failure(DomainException(DomainError.Server(409)))
            }
        )
        fillValidForm(viewModel)

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onRegisterButtonClick()

            val event = awaitItem() as RegisterUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_server)
        }
    }

    @Test
    fun `onTwitchLoginClick when the URL is ready should emit OpenCustomTab with it`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            startTwitchLogin = { Result.success("https://api/auth/twitch/login?code_challenge=abc") }
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onTwitchLoginClick()

            val event = awaitItem() as RegisterUiEvent.OpenCustomTab
            assertThat(event.url).isEqualTo("https://api/auth/twitch/login?code_challenge=abc")
        }
    }

    @Test
    fun `onTwitchLoginClick when preparing the flow fails should emit the OAuth failure snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            startTwitchLogin = { Result.failure(Exception("keystore indisponible")) }
        )

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onTwitchLoginClick()

            val event = awaitItem() as RegisterUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.oauth_error_failed)
        }
    }

    private companion object {
        val fakeRegisterResult = LoginResultEntity(
            message = "",
            token = "fake_jwt_token",
            user = UserEntity(
                id = "1",
                userName = "Inox",
                email = "inox@test.com",
                role = UserRole.VIEWER,
                avatarUrl = null
            )
        )
    }
}
