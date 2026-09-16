package com.strimup.core.network

import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

private const val HTTP_UNAUTHORIZED = 401

fun Throwable.toDomainError(): DomainError = when (this) {
    is DomainException -> error
    is SocketTimeoutException -> DomainError.Timeout
    is HttpException -> if (code() == HTTP_UNAUTHORIZED) {
        DomainError.Unauthorized
    } else {
        DomainError.Server(code())
    }
    is SerializationException -> DomainError.Serialization
    is IOException -> DomainError.Network
    else -> DomainError.Unknown
}

fun <T> Result<T>.toDomainResult(): Result<T> {
    val exception = exceptionOrNull()
    return when {
        exception == null -> this
        exception is DomainException -> this
        else -> Result.failure(DomainException(exception.toDomainError()))
    }
}
