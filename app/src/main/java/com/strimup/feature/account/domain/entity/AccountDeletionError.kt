package com.strimup.feature.account.domain.entity

sealed interface AccountDeletionError {
    data object InvalidConfirmation : AccountDeletionError
    data class InvalidPassword(val message: String?) : AccountDeletionError
    data class NotAllowed(val message: String?) : AccountDeletionError
    data class PendingObligations(val message: String?) : AccountDeletionError
    data object TooManyAttempts : AccountDeletionError
    data object Network : AccountDeletionError
    data object Unknown : AccountDeletionError
}

class AccountDeletionException(val error: AccountDeletionError) : Exception()
