package com.strimup.feature.account.presentation.deletion

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.feature.account.R
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy
import com.strimup.feature.account.domain.usecase.DeleteAccountUseCase
import com.strimup.feature.account.domain.usecase.GetAccountDeletionPolicyUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import com.strimup.core.ui.R as CoreUiR

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteAccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val passwordPolicy = AccountDeletionPolicy(isPasswordRequired = true)
    private val twitchPolicy = AccountDeletionPolicy(isPasswordRequired = false)

    private data class DeleteCall(val confirmation: String, val password: String?)

    private fun buildViewModel(
        getDeletionPolicy: GetAccountDeletionPolicyUseCase = GetAccountDeletionPolicyUseCase {
            Result.success(passwordPolicy)
        },
        deleteAccount: DeleteAccountUseCase = DeleteAccountUseCase { _, _ -> Result.success(Unit) },
    ) = DeleteAccountViewModel(
        getDeletionPolicy = getDeletionPolicy,
        deleteAccount = deleteAccount,
    )

    private fun failingWith(error: AccountDeletionError) =
        DeleteAccountUseCase { _, _ -> Result.failure(AccountDeletionException(error)) }

    @Test
    fun `init should expose the loaded deletion policy`() = runTest {
        val viewModel = buildViewModel()

        assertThat(viewModel.state.value.isPolicyLoading).isTrue()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isPolicyLoading).isFalse()
        assertThat(state.policy).isEqualTo(passwordPolicy)
        assertThat(state.isPasswordRequired).isTrue()
    }

    @Test
    fun `init when the policy cannot be loaded should expose the mapped error`() = runTest {
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.failure(DomainException(DomainError.Network)) },
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isPolicyLoading).isFalse()
        assertThat(state.policyErrorRes).isEqualTo(CoreUiR.string.error_network)
        assertThat(state.isSubmitEnabled).isFalse()
    }

    @Test
    fun `onRetryClick should reload the deletion policy`() = runTest {
        var callCount = 0
        val viewModel = buildViewModel(
            getDeletionPolicy = {
                callCount++
                if (callCount == 1) {
                    Result.failure(DomainException(DomainError.Network))
                } else {
                    Result.success(twitchPolicy)
                }
            },
        )
        advanceUntilIdle()

        viewModel.onRetryClick()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.policyErrorRes).isNull()
        assertThat(state.policy).isEqualTo(twitchPolicy)
    }

    @Test
    fun `submit should stay disabled until the confirmation is exactly SUPPRIMER`() = runTest {
        val viewModel = buildViewModel(getDeletionPolicy = { Result.success(twitchPolicy) })
        advanceUntilIdle()

        listOf("", "SUPPRIME", "supprimer", "Supprimer", "SUPPRIMER ", " SUPPRIMER").forEach { input ->
            viewModel.onConfirmationChange(input)
            assertThat(viewModel.state.value.isSubmitEnabled).isFalse()
        }

        viewModel.onConfirmationChange("SUPPRIMER")
        assertThat(viewModel.state.value.isSubmitEnabled).isTrue()
    }

    @Test
    fun `submit should stay disabled without password when the policy requires one`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onConfirmationChange("SUPPRIMER")
        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()

        viewModel.onPasswordChange("Secret123!")
        assertThat(viewModel.state.value.isSubmitEnabled).isTrue()
    }

    @Test
    fun `onDeleteClick when submit is disabled should not call the use case`() = runTest {
        val calls = mutableListOf<DeleteCall>()
        val viewModel = buildViewModel(
            deleteAccount = { confirmation, password ->
                calls += DeleteCall(confirmation, password)
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        viewModel.onConfirmationChange("supprimer")
        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(calls).isEmpty()
    }

    @Test
    fun `onDeleteClick for an e-mail account should send the password and emit AccountDeleted`() = runTest {
        val calls = mutableListOf<DeleteCall>()
        val viewModel = buildViewModel(
            deleteAccount = { confirmation, password ->
                calls += DeleteCall(confirmation, password)
                Result.success(Unit)
            },
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")
        viewModel.onPasswordChange("Secret123!")

        viewModel.events.test {
            viewModel.onDeleteClick()
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(DeleteAccountUiEvent.AccountDeleted)
        }
        assertThat(calls).containsExactly(DeleteCall("SUPPRIMER", "Secret123!"))
    }

    @Test
    fun `onDeleteClick for a Twitch account should never send a password`() = runTest {
        val calls = mutableListOf<DeleteCall>()
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.success(twitchPolicy) },
            deleteAccount = { confirmation, password ->
                calls += DeleteCall(confirmation, password)
                Result.success(Unit)
            },
        )
        advanceUntilIdle()
        viewModel.onPasswordChange("typed by mistake")
        viewModel.onConfirmationChange("SUPPRIMER")

        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(calls).containsExactly(DeleteCall("SUPPRIMER", null))
    }

    @Test
    fun `onDeleteClick should lock the form while the deletion is running`() = runTest {
        val pendingResult = CompletableDeferred<Result<Unit>>()
        var callCount = 0
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.success(twitchPolicy) },
            deleteAccount = { _, _ ->
                callCount++
                pendingResult.await()
            },
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")

        viewModel.onDeleteClick()
        advanceUntilIdle()
        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isDeleting).isTrue()
        assertThat(viewModel.state.value.isSubmitEnabled).isFalse()
        assertThat(callCount).isEqualTo(1)

        pendingResult.complete(Result.success(Unit))
        advanceUntilIdle()
    }

    @Test
    fun `onDeleteClick with a wrong password should show the backend message and allow a retry`() = runTest {
        val viewModel = buildViewModel(
            deleteAccount = failingWith(AccountDeletionError.InvalidPassword("Mot de passe incorrect")),
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")
        viewModel.onPasswordChange("wrong")

        viewModel.onDeleteClick()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.errorMessage).isEqualTo(UiText.Dynamic("Mot de passe incorrect"))
        assertThat(state.isDeleting).isFalse()
        assertThat(state.isSubmitEnabled).isTrue()
    }

    @Test
    fun `onDeleteClick with a wrong password and no backend message should show the default message`() = runTest {
        val viewModel = buildViewModel(deleteAccount = failingWith(AccountDeletionError.InvalidPassword(null)))
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")
        viewModel.onPasswordChange("wrong")

        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage)
            .isEqualTo(UiText.Resource(R.string.account_deletion_error_invalid_password))
    }

    @Test
    fun `onDeleteClick with pending obligations should show the backend message`() = runTest {
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.success(twitchPolicy) },
            deleteAccount = failingWith(AccountDeletionError.PendingObligations("Commande UGC en cours")),
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")

        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Dynamic("Commande UGC en cours"))
    }

    @Test
    fun `onDeleteClick with too many attempts should ask to retry later`() = runTest {
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.success(twitchPolicy) },
            deleteAccount = failingWith(AccountDeletionError.TooManyAttempts),
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")

        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage)
            .isEqualTo(UiText.Resource(R.string.account_deletion_error_too_many_attempts))
    }

    @Test
    fun `onDeleteClick when offline should show the network error and not emit AccountDeleted`() = runTest {
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.success(twitchPolicy) },
            deleteAccount = failingWith(AccountDeletionError.Network),
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")

        viewModel.events.test {
            viewModel.onDeleteClick()
            advanceUntilIdle()

            expectNoEvents()
        }
        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_network))
    }

    @Test
    fun `onDeleteClick with an unexpected exception should show the generic error`() = runTest {
        val viewModel = buildViewModel(
            getDeletionPolicy = { Result.success(twitchPolicy) },
            deleteAccount = { _, _ -> Result.failure(IllegalStateException()) },
        )
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")

        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Resource(CoreUiR.string.error_unknown))
    }

    @Test
    fun `editing an input should clear the previous error`() = runTest {
        val viewModel = buildViewModel(deleteAccount = failingWith(AccountDeletionError.InvalidPassword(null)))
        advanceUntilIdle()
        viewModel.onConfirmationChange("SUPPRIMER")
        viewModel.onPasswordChange("wrong")
        viewModel.onDeleteClick()
        advanceUntilIdle()

        viewModel.onPasswordChange("Secret123!")

        assertThat(viewModel.state.value.errorMessage).isNull()
    }

    @Test
    fun `onPasswordVisibleChange should update the password visibility`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onPasswordVisibleChange(true)

        assertThat(viewModel.state.value.isPasswordVisible).isTrue()
    }
}
