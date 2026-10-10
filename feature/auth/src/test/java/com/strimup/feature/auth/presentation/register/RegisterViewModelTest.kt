package com.strimup.feature.auth.presentation.register

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.R
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import com.strimup.feature.auth.domain.usecase.RegisterUseCase
import com.strimup.feature.auth.domain.usecase.StartTwitchLoginUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import com.strimup.core.ui.R as CoreUiR

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
        viewModel.onTermsAcceptedChange(true)
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
    fun `isFormValid should be false when passwords do not match`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        // WHEN
        viewModel.onConfirmPasswordChange("different")

        // THEN
        assertThat(viewModel.state.value.isFormValid).isFalse()
    }

    @Test
    fun `isFormValid should be false when terms are not accepted`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        // WHEN
        viewModel.onTermsAcceptedChange(false)

        // THEN
        assertThat(viewModel.state.value.isFormValid).isFalse()
    }

    @Test
    fun `onRegisterButtonClick without accepted terms should not execute register`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                useCaseCalled = true
                Result.success(fakeRegisterResult)
            }
        )
        fillValidForm(viewModel)
        viewModel.onTermsAcceptedChange(false)

        // WHEN
        viewModel.onRegisterButtonClick()

        // THEN
        assertThat(useCaseCalled).isFalse()
    }

    @Test
    fun `onRegisterButtonClick should send the accepted terms with the credentials`() = runTest {
        var capturedCredentials: RegisterCredentials? = null
        val viewModel = buildViewModel(
            register = RegisterUseCase { credentials ->
                capturedCredentials = credentials
                Result.success(fakeRegisterResult)
            }
        )
        fillValidForm(viewModel)

        viewModel.events.test {
            viewModel.onRegisterButtonClick()
            awaitItem()
        }

        assertThat(capturedCredentials?.hasAcceptedTerms).isTrue()
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
    fun `onRegisterButtonClick when register fails should show the mapped error under the form`() = runTest {
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ -> Result.failure(Exception("peu importe")) }
        )
        fillValidForm(viewModel)

        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_unknown))
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `onRegisterButtonClick when the backend rejects the form should show its message`() = runTest {
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                Result.failure(DomainException(DomainError.Server(400, "Email déjà utilisé")))
            }
        )
        fillValidForm(viewModel)

        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Dynamic("Email déjà utilisé"))
    }

    @Test
    fun `onRegisterButtonClick when the server crashes should not show its technical message`() = runTest {
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                Result.failure(DomainException(DomainError.Server(500, "Cannot read properties of undefined")))
            }
        )
        fillValidForm(viewModel)

        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_server))
    }

    @Test
    fun `editing the form should clear the backend error`() = runTest {
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                Result.failure(DomainException(DomainError.Server(400, "Pseudo déjà utilisé")))
            }
        )
        fillValidForm(viewModel)
        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        viewModel.onPseudoChange("Inox2")

        assertThat(viewModel.state.value.errorMessage).isNull()
    }

    @Test
    fun `onRegisterButtonClick should trim the pseudo and the e-mail`() = runTest {
        var capturedCredentials: RegisterCredentials? = null
        val viewModel = buildViewModel(
            register = RegisterUseCase { credentials ->
                capturedCredentials = credentials
                Result.success(fakeRegisterResult)
            }
        )
        fillValidForm(viewModel)
        viewModel.onPseudoChange("  Inox ")
        viewModel.onEmailChange(" inox@test.com  ")

        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        assertThat(capturedCredentials?.userName).isEqualTo("Inox")
        assertThat(capturedCredentials?.email).isEqualTo("inox@test.com")
    }

    @Test
    fun `password with a forbidden character should block the submit with a clear message`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        viewModel.onPasswordChange("Motdepasse#2026")
        viewModel.onConfirmPasswordChange("Motdepasse#2026")

        val state = viewModel.state.value
        assertThat(state.isFormValid).isFalse()
        assertThat(state.passwordError)
            .isEqualTo(UiText.Resource(R.string.password_error_forbidden_character, listOf("#")))
    }

    @Test
    fun `password containing the pseudo should block the submit`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        viewModel.onPasswordChange("SuperInox!2026")
        viewModel.onConfirmPasswordChange("SuperInox!2026")

        val state = viewModel.state.value
        assertThat(state.isFormValid).isFalse()
        assertThat(state.passwordError).isEqualTo(UiText.Resource(R.string.password_error_personal_information))
    }

    @Test
    fun `reserved pseudo should block the submit`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        viewModel.onPseudoChange("Admin")

        val state = viewModel.state.value
        assertThat(state.isFormValid).isFalse()
        assertThat(state.pseudoError).isEqualTo(UiText.Resource(R.string.pseudo_error_reserved))
    }

    @Test
    fun `streamer younger than 16 should block the submit with the age message`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        viewModel.onRoleChange(UserRole.STREAMER)
        viewModel.onBirthDateChange(LocalDate.now().minusYears(15).toString())

        val state = viewModel.state.value
        assertThat(state.isFormValid).isFalse()
        assertThat(state.birthDateError).isEqualTo(UiText.Resource(R.string.birth_date_error_too_young, listOf(16)))
    }

    @Test
    fun `viewer of 15 should be allowed to register`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        viewModel.onRoleChange(UserRole.VIEWER)
        viewModel.onBirthDateChange(LocalDate.now().minusYears(15).toString())

        assertThat(viewModel.state.value.birthDateError).isNull()
        assertThat(viewModel.state.value.isFormValid).isTrue()
    }

    @Test
    fun `confirmation different from the password should show a mismatch message`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)

        viewModel.onConfirmPasswordChange("Password123?")

        assertThat(viewModel.state.value.confirmPasswordError)
            .isEqualTo(UiText.Resource(R.string.password_error_mismatch))
    }

    @Test
    fun `errors of untouched fields should stay hidden before the first submit`() = runTest {
        val viewModel = buildViewModel()

        val state = viewModel.state.value
        assertThat(state.pseudoError).isNull()
        assertThat(state.emailError).isNull()
        assertThat(state.genderError).isNull()
        assertThat(state.termsError).isNull()
    }

    @Test
    fun `onRegisterButtonClick with an empty form should reveal every missing field without registering`() = runTest {
        var useCaseCalled = false
        val viewModel = buildViewModel(
            register = RegisterUseCase { _ ->
                useCaseCalled = true
                Result.success(fakeRegisterResult)
            }
        )

        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(useCaseCalled).isFalse()
        assertThat(state.pseudoError).isEqualTo(UiText.Resource(R.string.pseudo_error_required))
        assertThat(state.emailError).isEqualTo(UiText.Resource(R.string.email_error_required))
        assertThat(state.birthDateError).isEqualTo(UiText.Resource(R.string.birth_date_error_required))
        assertThat(state.genderError).isEqualTo(UiText.Resource(R.string.gender_error_required))
        assertThat(state.roleError).isEqualTo(UiText.Resource(R.string.role_error_required))
        assertThat(state.passwordError).isEqualTo(UiText.Resource(R.string.password_error_required))
        assertThat(state.confirmPasswordError)
            .isEqualTo(UiText.Resource(R.string.password_error_confirmation_required))
        assertThat(state.termsError).isEqualTo(UiText.Resource(R.string.terms_error_required))
    }

    @Test
    fun `onRegisterButtonClick without accepted terms should only flag the terms`() = runTest {
        val viewModel = buildViewModel()
        fillValidForm(viewModel)
        viewModel.onTermsAcceptedChange(false)

        viewModel.onRegisterButtonClick()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.termsError).isEqualTo(UiText.Resource(R.string.terms_error_required))
        assertThat(state.pseudoError).isNull()
        assertThat(state.passwordError).isNull()
        assertThat(state.genderError).isNull()
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
            assertThat(event.textRes).isEqualTo(CoreUiR.string.oauth_error_failed)
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
