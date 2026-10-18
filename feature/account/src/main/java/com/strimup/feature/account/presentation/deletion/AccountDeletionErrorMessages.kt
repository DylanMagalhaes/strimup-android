package com.strimup.feature.account.presentation.deletion

import androidx.annotation.StringRes
import com.strimup.core.ui.text.UiText
import com.strimup.feature.account.R
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.core.ui.R as CoreUiR

fun AccountDeletionError.toUiText(): UiText = when (this) {
    is AccountDeletionError.InvalidPassword ->
        message.orResource(R.string.account_deletion_error_invalid_password)

    is AccountDeletionError.NotAllowed ->
        message.orResource(R.string.account_deletion_error_not_allowed)

    is AccountDeletionError.PendingObligations ->
        message.orResource(R.string.account_deletion_error_pending_obligations)

    AccountDeletionError.TooManyAttempts -> UiText.Resource(R.string.account_deletion_error_too_many_attempts)
    AccountDeletionError.Network -> UiText.Resource(CoreUiR.string.error_network)
    AccountDeletionError.InvalidConfirmation,
    AccountDeletionError.Unknown -> UiText.Resource(CoreUiR.string.error_unknown)
}

private fun String?.orResource(@StringRes fallbackResId: Int): UiText {
    return this?.let(UiText::Dynamic) ?: UiText.Resource(fallbackResId)
}
