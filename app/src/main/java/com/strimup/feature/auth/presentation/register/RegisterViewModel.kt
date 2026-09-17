package com.strimup.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.entity.RegisterCredentials
import com.strimup.feature.auth.domain.usecase.RegisterUseCase
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
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegisterUiEvent>()
    val events = _events.receiveAsFlow()

    fun onPseudoChange(pseudo: String) {
        _state.update { it.copy(pseudoInput = pseudo) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(emailInput = email) }
    }

    fun onBirthDateChange(birthDate: String) {
        _state.update { it.copy(birthDateInput = birthDate) }
    }

    fun onGenderChange(gender: Gender) {
        _state.update { it.copy(genderInput = gender) }
    }

    fun onRoleChange(role: UserRole) {
        _state.update { it.copy(roleInput = role) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(passwordInput = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPasswordInput = confirmPassword) }
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

        if (!currentState.isSubmitEnabled || gender == null || role == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val credentials = RegisterCredentials(
                userName = currentState.pseudoInput,
                email = currentState.emailInput,
                password = currentState.passwordInput,
                birthDate = currentState.birthDateInput,
                gender = gender,
                role = role,
            )

            register(credentials)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(RegisterUiEvent.ShowHomeUi)
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    val messageRes = exception.toDomainError().toMessageRes()
                    _events.send(RegisterUiEvent.ShowSnackBar(messageRes))
                }
        }
    }
}

enum class RegisterDropdown { DATE, SEX, ROLE }

enum class RegisterPasswordField { PASSWORD, CONFIRM_PASSWORD }
