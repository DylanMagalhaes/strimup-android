package com.strimup.core.network

import com.strimup.core.common.DomainError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

private const val HTTP_UNAUTHORIZED = 401

fun Throwable.toDomainError(): DomainError = when (this) {
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
