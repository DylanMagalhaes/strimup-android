package com.strimup.feature.account.presentation.deletion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import com.strimup.feature.account.domain.usecase.DeleteAccountUseCase
import com.strimup.feature.account.domain.usecase.GetAccountDeletionPolicyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val getDeletionPolicy: GetAccountDeletionPolicyUseCase,
    private val deleteAccount: DeleteAccountUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DeleteAccountUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<DeleteAccountUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadPolicy()
    }

    fun onRetryClick() {
        loadPolicy()
    }

    fun onConfirmationChange(confirmation: String) {
        _state.update { it.copy(confirmationInput = confirmation, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun onPasswordVisibleChange(isVisible: Boolean) {
        _state.update { it.copy(isPasswordVisible = isVisible) }
    }

    fun onDeleteClick() {
        val currentState = _state.value
        if (!currentState.isSubmitEnabled) return

        _state.update { it.copy(isDeleting = true, errorMessage = null) }

        viewModelScope.launch {
            deleteAccount(
                confirmation = currentState.confirmationInput,
                password = currentState.passwordInput.takeIf { currentState.isPasswordRequired },
            )
                .onSuccess {
                    _events.send(DeleteAccountUiEvent.AccountDeleted)
                }
                .onFailure { exception ->
                    val error = (exception as? AccountDeletionException)?.error
                        ?: AccountDeletionError.Unknown
                    _state.update { it.copy(isDeleting = false, errorMessage = error.toUiText()) }
                }
        }
    }

    private fun loadPolicy() {
        _state.update { it.copy(isPolicyLoading = true, policyErrorRes = null) }

        viewModelScope.launch {
            getDeletionPolicy()
                .onSuccess { policy ->
                    _state.update { it.copy(policy = policy, isPolicyLoading = false) }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isPolicyLoading = false,
                            policyErrorRes = exception.toDomainError().toMessageRes(),
                        )
                    }
                }
        }
    }
}
