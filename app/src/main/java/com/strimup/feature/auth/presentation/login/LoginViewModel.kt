package com.strimup.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.auth.domain.usecase.LoginUsecase
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
    private val login: LoginUsecase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<LoginUiEvent>()
    val event = _events.receiveAsFlow()

    fun onLoginButtonClick() {
        val email = _state.value.emailInput
        val password = _state.value.passwordInput

        if (email.isBlank() || password.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            login(email, password)
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = response.user,
                        )
                    }
                    _events.send(LoginUiEvent.ShowHomeUi)
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    val errorMessage = exception.localizedMessage ?: "Une erreur est survenue"
                    _events.send(LoginUiEvent.ShowSnackBar(text = errorMessage))
                }
        }
    }

    fun onEmailChange(email: String){
        _state.update {
            it.copy(emailInput = email)
        }
    }

    fun onPasswordChange(password: String){
        _state.update {
            it.copy(passwordInput = password)
        }
    }
}