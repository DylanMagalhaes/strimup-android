package com.strimup.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.feature.auth.domain.usecase.LoginUseCase
import com.strimup.feature.auth.domain.usecase.StartTwitchLoginUseCase
import com.strimup.feature.auth.presentation.AuthWebUrls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val startTwitchLogin: StartTwitchLoginUseCase,
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
                    _events.send(LoginUiEvent.ShowSnackBar(exception.toLoginErrorUiText()))
                }
        }
    }

    fun onTwitchLoginClick() {
        viewModelScope.launch {
            startTwitchLogin()
                .onSuccess { url ->
                    _events.send(LoginUiEvent.OpenCustomTab(url))
                }
                .onFailure {
                    _events.send(LoginUiEvent.ShowSnackBar(UiText.Resource(R.string.oauth_error_failed)))
                }
        }
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch {
            _events.send(LoginUiEvent.OpenCustomTab(AuthWebUrls.FORGOT_PASSWORD))
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

private const val HTTP_UNAUTHORIZED = 401

private fun Throwable.toLoginErrorUiText(): UiText {
    val error = toDomainError()
    if (error is DomainError.Server && error.code == HTTP_UNAUTHORIZED) {
        return error.message?.let(UiText::Dynamic) ?: UiText.Resource(R.string.error_invalid_credentials)
    }
    return error.toUiText()
}
