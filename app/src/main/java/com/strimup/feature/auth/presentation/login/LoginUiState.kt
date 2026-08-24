package com.strimup.feature.auth.presentation.login

import com.strimup.core.user.domain.entity.UserEntity

data class LoginUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
) {
    val isSubmitEnabled: Boolean
        get() = emailInput.isNotBlank() && passwordInput.isNotBlank() && !isLoading
}