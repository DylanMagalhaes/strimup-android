package com.strimup.core.common

sealed class DomainError {

    data object Network : DomainError()
    data object Timeout : DomainError()
    data object Unauthorized : DomainError()
    data class Server(val code: Int, val message: String? = null) : DomainError()
    data object Serialization : DomainError()
    data object Unknown : DomainError()
}
