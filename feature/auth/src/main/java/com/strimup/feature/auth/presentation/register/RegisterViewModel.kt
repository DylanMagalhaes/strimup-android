package com.strimup.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.R
import com.strimup.core.ui.error.toUiText
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import com.strimup.feature.auth.domain.usecase.RegisterUseCase
import com.strimup.feature.auth.domain.usecase.StartTwitchLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: RegisterUseCase,
    private val startTwitchLogin: StartTwitchLoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegisterUiEvent>()
    val events = _events.receiveAsFlow()

    fun onPseudoChange(pseudo: String) {
        updateForm { it.copy(pseudoInput = pseudo) }
    }

    fun onEmailChange(email: String) {
        updateForm { it.copy(emailInput = email) }
    }

    fun onBirthDateChange(birthDate: String) {
        updateForm { it.copy(birthDateInput = birthDate) }
    }

    fun onGenderChange(gender: Gender) {
        updateForm { it.copy(genderInput = gender) }
    }

    fun onRoleChange(role: UserRole) {
        updateForm { it.copy(roleInput = role) }
    }

    fun onPasswordChange(password: String) {
        updateForm { it.copy(passwordInput = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        updateForm { it.copy(confirmPasswordInput = confirmPassword) }
    }

    fun onTermsAcceptedChange(isAccepted: Boolean) {
        updateForm { it.copy(isTermsAccepted = isAccepted) }
    }

    fun onPasswordVisibleChange(field: RegisterPasswordField, isVisible: Boolean) {
        _state.update {
            when (field) {
                RegisterPasswordField.PASSWORD -> it.copy(isPasswordVisible = isVisible)
                RegisterPasswordField.CONFIRM_PASSWORD -> it.copy(isConfirmPasswordVisible = isVisible)
            }
        }
    }

    fun onDropdownExpendedChange(dropdown: RegisterDropdown, isExpended: Boolean) {
        _state.update {
            when (dropdown) {
                RegisterDropdown.DATE -> it.copy(isDateDropDownExpended = isExpended)
                RegisterDropdown.SEX -> it.copy(isSexDropDownExpended = isExpended)
                RegisterDropdown.ROLE -> it.copy(isRoleDropDownExpended = isExpended)
            }
        }
    }

    fun onRegisterButtonClick() {
        val currentState = _state.value
        val gender = currentState.genderInput
        val role = currentState.roleInput

        if (currentState.isLoading) return

        if (!currentState.isFormValid || gender == null || role == null) {
            _state.update { it.copy(hasTriedToSubmit = true) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val credentials = RegisterCredentials(
                userName = currentState.pseudoInput.trim(),
                email = currentState.emailInput.trim(),
                password = currentState.passwordInput,
                birthDate = currentState.birthDateInput,
                gender = gender,
                role = role,
                hasAcceptedTerms = currentState.isTermsAccepted,
            )

            register(credentials)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(RegisterUiEvent.ShowHomeUi)
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = exception.toDomainError().toUiText())
                    }
                }
        }
    }

    fun onTwitchLoginClick() {
        viewModelScope.launch {
            startTwitchLogin()
                .onSuccess { url ->
                    _events.send(RegisterUiEvent.OpenCustomTab(url))
                }
                .onFailure {
                    _events.send(RegisterUiEvent.ShowSnackBar(R.string.oauth_error_failed))
                }
        }
    }

    private fun updateForm(transform: (RegisterUiState) -> RegisterUiState) {
        _state.update { transform(it).copy(errorMessage = null) }
    }
}

enum class RegisterDropdown { DATE, SEX, ROLE }

enum class RegisterPasswordField { PASSWORD, CONFIRM_PASSWORD }
