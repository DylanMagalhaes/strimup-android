package com.strimup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val logout: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = Channel<MainUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            getUser().collect { user ->
                _state.update {
                    it.copy(
                        loading = false,
                        user = user
                    )
                }
            }

        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logout()
                .onSuccess {
                    _events.send(MainUiEvent.LoggedOut)
                }
                .onFailure { exception ->
                    val messageRes = exception.toDomainError().toMessageRes()
                    _events.send(MainUiEvent.ShowSnackBar(messageRes))
                }
        }
    }

}
