package com.strimup.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.entity.OAuthCallback
import com.strimup.feature.auth.domain.entity.OAuthFailureReason
import com.strimup.feature.auth.domain.usecase.ExchangeOAuthCodeUseCase
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
    private val exchangeOAuthCode: ExchangeOAuthCodeUseCase,
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

    fun onOAuthCallback(callback: OAuthCallback?) {
        when (callback) {
            is OAuthCallback.LoggedIn -> exchangeCode(
                code = callback.code,
                successEvent = MainUiEvent.OAuthLoggedIn,
            )

            is OAuthCallback.Linked -> exchangeCode(
                code = callback.code,
                successEvent = MainUiEvent.ShowSnackBar(R.string.oauth_link_success),
            )

            is OAuthCallback.Onboarding -> viewModelScope.launch {
                _events.send(MainUiEvent.OAuthOnboardingRequired(callback.tmp))
            }

            is OAuthCallback.Failed -> viewModelScope.launch {
                _events.send(MainUiEvent.ShowSnackBar(callback.reason.toMessageRes()))
            }

            null -> Unit
        }
    }

    private fun exchangeCode(code: String, successEvent: MainUiEvent) {
        viewModelScope.launch {
            exchangeOAuthCode(code)
                .onSuccess {
                    _events.send(successEvent)
                }
                .onFailure { exception ->
                    _events.send(MainUiEvent.ShowSnackBar(exception.toOAuthMessageRes()))
                }
        }
    }
}

@StringRes
private fun Throwable.toOAuthMessageRes(): Int = when (val error = toDomainError()) {
    is DomainError.Server -> if (error.code in 400..499) R.string.oauth_error_failed else error.toMessageRes()
    DomainError.Unknown -> R.string.oauth_error_failed
    else -> error.toMessageRes()
}

@StringRes
private fun OAuthFailureReason.toMessageRes(): Int = when (this) {
    OAuthFailureReason.ACCESS_DENIED -> R.string.oauth_error_access_denied
    OAuthFailureReason.INVALID_STATE -> R.string.oauth_error_expired
    OAuthFailureReason.INVALID_REQUEST,
    OAuthFailureReason.SERVER_ERROR,
    OAuthFailureReason.UNKNOWN -> R.string.oauth_error_failed
}
