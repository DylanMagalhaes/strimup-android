package com.strimup.feature.auth.presentation.oauthonboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.entity.OAuthCredentials
import com.strimup.feature.auth.domain.usecase.CompleteOAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OAuthOnboardingViewModel @Inject constructor(
    private val completeOAuth: CompleteOAuthUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OAuthOnboardingUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<OAuthOnboardingUiEvent>()
    val events = _events.receiveAsFlow()

    fun onBirthDateChange(birthDate: String) {
        _state.update { it.copy(birthDateInput = birthDate) }
    }

    fun onGenderChange(gender: Gender) {
        _state.update { it.copy(genderInput = gender) }
    }

    fun onRoleChange(role: UserRole) {
        _state.update { it.copy(roleInput = role) }
    }

    fun onDropdownExpendedChange(dropdown: OAuthOnboardingDropdown, isExpended: Boolean) {
        _state.update {
            when (dropdown) {
                OAuthOnboardingDropdown.DATE -> it.copy(isDateDropDownExpended = isExpended)
                OAuthOnboardingDropdown.SEX -> it.copy(isSexDropDownExpended = isExpended)
                OAuthOnboardingDropdown.ROLE -> it.copy(isRoleDropDownExpended = isExpended)
            }
        }
    }

    fun onSubmitClick(tmp: String) {
        val currentState = _state.value
        val gender = currentState.genderInput
        val role = currentState.roleInput

        if (!currentState.isSubmitEnabled || gender == null || role == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val credentials = OAuthCredentials(
                tmp = tmp,
                role = role,
                birthDate = currentState.birthDateInput,
                gender = gender,
                hasAcceptedTerms = true,
            )

            completeOAuth(credentials)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(OAuthOnboardingUiEvent.Completed)
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    val messageRes = exception.toDomainError().toMessageRes()
                    _events.send(OAuthOnboardingUiEvent.ShowSnackBar(messageRes))
                }
        }
    }
}

enum class OAuthOnboardingDropdown { DATE, SEX, ROLE }
