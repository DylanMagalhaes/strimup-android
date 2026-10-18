package com.strimup.feature.auth.presentation.register

import androidx.annotation.StringRes
import com.strimup.core.ui.text.UiText
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.R
import com.strimup.feature.auth.domain.PasswordCheck
import com.strimup.feature.auth.domain.PasswordIdentity
import com.strimup.feature.auth.domain.passwordChecklist
import com.strimup.feature.auth.domain.validateBirthDate
import com.strimup.feature.auth.domain.validatePassword
import com.strimup.feature.auth.domain.validatePseudo
import com.strimup.feature.auth.presentation.toErrorUiText
import com.strimup.feature.auth.presentation.toUiText
import java.time.LocalDate

data class RegisterUiState(
    val pseudoInput: String = "",
    val emailInput: String = "",
    val birthDateInput: String = "",
    val genderInput: Gender? = null,
    val roleInput: UserRole? = null,
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isTermsAccepted: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isDateDropDownExpended: Boolean = false,
    val isSexDropDownExpended: Boolean = false,
    val isRoleDropDownExpended: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null,
    val hasTriedToSubmit: Boolean = false,
    val today: LocalDate = LocalDate.now(),
) {
    private val passwordIdentity: PasswordIdentity
        get() = PasswordIdentity(pseudo = pseudoInput, email = emailInput)

    val passwordChecklist: List<PasswordCheck>
        get() = passwordChecklist(passwordInput)

    val pseudoError: UiText?
        get() = validatePseudo(pseudoInput)
            ?.takeIf { pseudoInput.isNotEmpty() || hasTriedToSubmit }
            ?.toUiText()

    val emailError: UiText?
        get() = requiredError(emailInput.isBlank(), R.string.email_error_required)

    val birthDateError: UiText?
        get() = validateBirthDate(birthDateInput, roleInput, today)
            ?.takeIf { birthDateInput.isNotEmpty() || hasTriedToSubmit }
            ?.toUiText(roleInput)

    val genderError: UiText?
        get() = requiredError(genderInput == null, R.string.gender_error_required)

    val roleError: UiText?
        get() = requiredError(roleInput == null, R.string.role_error_required)

    val passwordError: UiText?
        get() = if (passwordInput.isEmpty()) {
            requiredError(isMissing = true, messageRes = R.string.password_error_required)
        } else {
            validatePassword(passwordInput, passwordIdentity)?.toErrorUiText(passwordInput)
        }

    val confirmPasswordError: UiText?
        get() = when {
            confirmPasswordInput.isEmpty() -> requiredError(
                isMissing = true,
                messageRes = R.string.password_error_confirmation_required,
            )
            confirmPasswordInput != passwordInput -> UiText.Resource(R.string.password_error_mismatch)
            else -> null
        }

    val termsError: UiText?
        get() = requiredError(!isTermsAccepted, R.string.terms_error_required)

    val isFormValid: Boolean
        get() = validatePseudo(pseudoInput) == null &&
            emailInput.isNotBlank() &&
            validateBirthDate(birthDateInput, roleInput, today) == null &&
            genderInput != null &&
            roleInput != null &&
            validatePassword(passwordInput, passwordIdentity) == null &&
            passwordInput == confirmPasswordInput &&
            isTermsAccepted

    private fun requiredError(isMissing: Boolean, @StringRes messageRes: Int): UiText? =
        UiText.Resource(messageRes).takeIf { isMissing && hasTriedToSubmit }
}
