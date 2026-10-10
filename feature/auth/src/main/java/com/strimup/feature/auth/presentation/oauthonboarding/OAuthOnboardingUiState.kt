package com.strimup.feature.auth.presentation.oauthonboarding

import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole

data class OAuthOnboardingUiState(
    val birthDateInput: String = "",
    val genderInput: Gender? = null,
    val roleInput: UserRole? = null,
    val isDateDropDownExpended: Boolean = false,
    val isSexDropDownExpended: Boolean = false,
    val isRoleDropDownExpended: Boolean = false,
    val isLoading: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() = birthDateInput.isNotBlank() &&
            genderInput != null &&
            roleInput != null &&
            !isLoading
}
