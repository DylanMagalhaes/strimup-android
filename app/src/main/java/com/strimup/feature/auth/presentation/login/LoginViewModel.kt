package com.strimup.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.auth.domain.usecase.LoginUsecase
import com.strimup.feature.auth.presentation.UiEvent
import com.strimup.feature.auth.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _events = Channel<UiEvent>()
    val event = _events.receiveAsFlow()

    fun onLoginButtonClick(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            login(email, password)
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            loading = false,
                            user = response.user,
                            isLoggedIn = true,
                        )
                    }

                }
                .onFailure { exception ->
                    _state.update { it.copy(loading = false) }

                    val errorMessage = "Une erreur est survenue"
                    _events.send(UiEvent.ShowSnackBar(text = errorMessage))
                }
        }
    }
}