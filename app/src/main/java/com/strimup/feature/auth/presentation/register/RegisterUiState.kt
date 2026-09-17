package com.strimup.feature.auth.presentation.register

import androidx.annotation.StringRes
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.PasswordCheck
import com.strimup.feature.auth.domain.passwordChecklist
import com.strimup.feature.auth.domain.validatePassword
import com.strimup.feature.auth.presentation.toErrorMessageRes

data class RegisterUiState(
    val pseudoInput: String = "",
    val emailInput: String = "",
    val birthDateInput: String = "",
    val genderInput: Gender? = null,
    val roleInput: UserRole? = null,
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isDateDropDownExpended: Boolean = false,
    val isSexDropDownExpended: Boolean = false,
    val isRoleDropDownExpended: Boolean = false,
    val isLoading: Boolean = false,
) {
    val passwordChecklist: List<PasswordCheck>
        get() = passwordChecklist(passwordInput)

    @get:StringRes
    val passwordErrorRes: Int?
        get() = if (passwordInput.isEmpty()) null else validatePassword(passwordInput)?.toErrorMessageRes()

    val isSubmitEnabled: Boolean
        get() = pseudoInput.isNotBlank() &&
            emailInput.isNotBlank() &&
            birthDateInput.isNotBlank() &&
            genderInput != null &&
            roleInput != null &&
            validatePassword(passwordInput) == null &&
            passwordInput == confirmPasswordInput &&
            !isLoading
}
