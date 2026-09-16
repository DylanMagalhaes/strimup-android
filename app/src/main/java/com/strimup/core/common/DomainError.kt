package com.strimup.core.common

sealed class DomainError {

    data object Network : DomainError()
    data object Timeout : DomainError()
    data object Unauthorized : DomainError()
    data class Server(val code: Int) : DomainError()
    data object Serialization : DomainError()
    data object Unknown : DomainError()
}
