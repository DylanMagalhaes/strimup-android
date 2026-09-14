package com.strimup.core.ui.error

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.core.common.DomainError

@StringRes
fun DomainError.toMessageRes(): Int = when (this) {
    DomainError.Network -> R.string.error_network
    DomainError.Timeout -> R.string.error_timeout
    DomainError.Unauthorized -> R.string.error_unauthorized
    is DomainError.Server -> R.string.error_server
    DomainError.Serialization -> R.string.error_serialization
    DomainError.Unknown -> R.string.error_unknown
}
