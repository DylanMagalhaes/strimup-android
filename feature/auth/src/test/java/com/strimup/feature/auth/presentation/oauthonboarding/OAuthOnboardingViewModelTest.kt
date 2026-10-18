package com.strimup.feature.auth.presentation.oauthonboarding

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.R
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.OAuthCredentials
import com.strimup.feature.auth.domain.usecase.CompleteOAuthUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OAuthOnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        completeOAuth: CompleteOAuthUseCase = CompleteOAuthUseCase { Result.success(fakeResult) },
    ) = OAuthOnboardingViewModel(completeOAuth = completeOAuth)

    private fun fillValidForm(viewModel: OAuthOnboardingViewModel) {
        viewModel.onBirthDateChange("1995-05-05")
        viewModel.onGenderChange(Gender.MALE)
        viewModel.onRoleChange(UserRole.VIEWER)
    }

    @Test
    fun `onBirthDateChange should update state birthDateInput`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        viewModel.onBirthDateChange("1995-05-05")

        // THEN
        assertThat(viewModel.state.value.birthDateInput).isEqualTo("1995-05-05")
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
    fun `onSubmitClick with incomplete form should not execute completeOAuth`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = buildViewModel(
            completeOAuth = CompleteOAuthUseCase {
                useCaseCalled = true
                Result.success(fakeResult)
            }
        )
        viewModel.onGenderChange(Gender.MALE)

        // WHEN
        viewModel.onSubmitClick("tmp-token")

        // THEN
        assertThat(useCaseCalled).isFalse()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `onSubmitClick when completeOAuth succeeds should pass the tmp token and reset isLoading and emit Completed`() =
        runTest {
            // GIVEN
            var capturedCredentials: OAuthCredentials? = null
            val viewModel = buildViewModel(
                completeOAuth = CompleteOAuthUseCase { credentials ->
                    capturedCredentials = credentials
                    Result.success(fakeResult)
                }
            )
            fillValidForm(viewModel)

            // WHEN & THEN
            viewModel.events.test {
                viewModel.onSubmitClick("tmp-token")

                val event = awaitItem()
                assertThat(event).isEqualTo(OAuthOnboardingUiEvent.Completed)
                assertThat(viewModel.state.value.isLoading).isFalse()
            }
            assertThat(capturedCredentials).isEqualTo(
                OAuthCredentials(
                    tmp = "tmp-token",
                    role = UserRole.VIEWER,
                    birthDate = "1995-05-05",
                    gender = Gender.MALE,
                    hasAcceptedTerms = true,
                )
            )
        }

    @Test
    fun `onSubmitClick when completeOAuth fails should emit ShowSnackBar with the mapped DomainError message`() =
        runTest {
            // GIVEN
            val viewModel = buildViewModel(
                completeOAuth = CompleteOAuthUseCase { Result.failure(Exception("peu importe")) }
            )
            fillValidForm(viewModel)

            // WHEN & THEN
            viewModel.events.test {
                viewModel.onSubmitClick("tmp-token")
                advanceUntilIdle()

                val event = awaitItem() as OAuthOnboardingUiEvent.ShowSnackBar
                assertThat(event.textRes).isEqualTo(R.string.error_unknown)
            }
            assertThat(viewModel.state.value.isLoading).isFalse()
        }

    @Test
    fun `onSubmitClick when completeOAuth fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            completeOAuth = CompleteOAuthUseCase { Result.failure(DomainException(DomainError.Server(409))) }
        )
        fillValidForm(viewModel)

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onSubmitClick("tmp-token")
            advanceUntilIdle()

            val event = awaitItem() as OAuthOnboardingUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_server)
        }
    }

    private companion object {
        val fakeResult = LoginResultEntity(
            message = "Connexion réussie",
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
