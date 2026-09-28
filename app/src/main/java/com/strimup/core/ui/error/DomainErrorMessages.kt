package com.strimup.core.ui.error

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.ui.text.UiText

@StringRes
fun DomainError.toMessageRes(): Int = when (this) {
    DomainError.Network -> R.string.error_network
    DomainError.Timeout -> R.string.error_timeout
    DomainError.Unauthorized -> R.string.error_unauthorized
    is DomainError.Server -> R.string.error_server
    DomainError.Serialization -> R.string.error_serialization
    DomainError.Unknown -> R.string.error_unknown
}

private const val HTTP_CLIENT_ERROR_MIN = 400
private const val HTTP_CLIENT_ERROR_MAX = 499
private val CLIENT_ERROR_CODES = HTTP_CLIENT_ERROR_MIN..HTTP_CLIENT_ERROR_MAX

fun DomainError.toUiText(): UiText {
    val serverMessage = (this as? DomainError.Server)
        ?.takeIf { it.code in CLIENT_ERROR_CODES }
        ?.message

    return serverMessage?.let(UiText::Dynamic) ?: UiText.Resource(toMessageRes())
}
