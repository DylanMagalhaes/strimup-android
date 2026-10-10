package com.strimup.feature.auth.data.mapper

import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.network.apiErrorMessage
import retrofit2.HttpException

private const val HTTP_UNAUTHORIZED = 401

fun Throwable.asInvalidCredentialsError(): Throwable =
    if (this is HttpException && code() == HTTP_UNAUTHORIZED) {
        DomainException(DomainError.Server(HTTP_UNAUTHORIZED, apiErrorMessage()))
    } else {
        this
    }
