package com.strimup.feature.account.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val logout: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<AccountUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            getUser().collect { user ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    fun onLogoutClick() {
        if (_state.value.isLoggingOut) return

        _state.update { it.copy(isLoggingOut = true) }

        viewModelScope.launch {
            logout()
                .onSuccess {
                    _events.send(AccountUiEvent.LoggedOut)
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoggingOut = false) }
                    _events.send(AccountUiEvent.ShowSnackBar(exception.toDomainError().toMessageRes()))
                }
        }
    }
}
