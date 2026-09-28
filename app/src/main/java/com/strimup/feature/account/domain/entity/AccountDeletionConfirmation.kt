package com.strimup.feature.account.domain.entity

object AccountDeletionConfirmation {
    const val WORD = "SUPPRIMER"

    fun isValid(input: String): Boolean = input == WORD
}
