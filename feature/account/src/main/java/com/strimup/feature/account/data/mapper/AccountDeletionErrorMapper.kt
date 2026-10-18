package com.strimup.feature.account.data.mapper

import com.strimup.core.network.apiErrorMessage
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403
private const val HTTP_NOT_FOUND = 404
private const val HTTP_CONFLICT = 409
private const val HTTP_TOO_MANY_REQUESTS = 429

fun Throwable.isAccountAlreadyDeleted(): Boolean {
    return this is HttpException && code() == HTTP_NOT_FOUND
}

fun Throwable.toAccountDeletionError(): AccountDeletionError = when (this) {
    is AccountDeletionException -> error
    is HttpException -> toAccountDeletionError()
    is IOException -> AccountDeletionError.Network
    else -> AccountDeletionError.Unknown
}

private fun HttpException.toAccountDeletionError(): AccountDeletionError = when (code()) {
    HTTP_UNAUTHORIZED -> AccountDeletionError.InvalidPassword(apiErrorMessage())
    HTTP_FORBIDDEN -> AccountDeletionError.NotAllowed(apiErrorMessage())
    HTTP_CONFLICT -> AccountDeletionError.PendingObligations(apiErrorMessage())
    HTTP_TOO_MANY_REQUESTS -> AccountDeletionError.TooManyAttempts
    else -> AccountDeletionError.Unknown
}

fun <T> Result<T>.toAccountDeletionResult(): Result<T> {
    val exception = exceptionOrNull() ?: return this
    if (exception is CancellationException) throw exception
    return Result.failure(AccountDeletionException(exception.toAccountDeletionError()))
}
