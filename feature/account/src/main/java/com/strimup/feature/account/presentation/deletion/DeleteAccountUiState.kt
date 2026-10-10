package com.strimup.feature.account.presentation.deletion

import androidx.annotation.StringRes
import com.strimup.core.ui.text.UiText
import com.strimup.feature.account.domain.entity.AccountDeletionConfirmation
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy

data class DeleteAccountUiState(
    val policy: AccountDeletionPolicy? = null,
    val isPolicyLoading: Boolean = true,
    @param:StringRes val policyErrorRes: Int? = null,
    val confirmationInput: String = "",
    val passwordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: UiText? = null,
) {
    val isPasswordRequired: Boolean
        get() = policy?.isPasswordRequired == true

    val isSubmitEnabled: Boolean
        get() = policy != null &&
            AccountDeletionConfirmation.isValid(confirmationInput) &&
            (!isPasswordRequired || passwordInput.isNotEmpty()) &&
            !isDeleting
}
