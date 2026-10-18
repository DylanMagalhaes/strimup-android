package com.strimup.feature.auth.domain

const val PASSWORD_SPECIAL_CHARACTERS = "@$!%*?&./-_"
private const val PASSWORD_MIN_LENGTH = 12
private const val PASSWORD_MAX_LENGTH = 128

data class PasswordIdentity(
    val pseudo: String = "",
    val email: String = "",
) {
    val forbiddenFragments: List<String>
        get() = listOf(pseudo.trim(), email.trim().substringBefore('@')).filter { it.isNotBlank() }
}

enum class PasswordRequirement(val isShownInChecklist: Boolean) {
    ALLOWED_CHARACTERS(isShownInChecklist = false),
    MIN_LENGTH(isShownInChecklist = true),
    MAX_LENGTH(isShownInChecklist = false),
    UPPERCASE(isShownInChecklist = true),
    LOWERCASE(isShownInChecklist = true),
    DIGIT(isShownInChecklist = true),
    SPECIAL_CHARACTER(isShownInChecklist = true),
    NO_PERSONAL_INFORMATION(isShownInChecklist = false);

    fun isSatisfiedBy(password: String, identity: PasswordIdentity = PasswordIdentity()): Boolean = when (this) {
        ALLOWED_CHARACTERS -> firstForbiddenPasswordCharacter(password) == null
        MIN_LENGTH -> password.length >= PASSWORD_MIN_LENGTH
        MAX_LENGTH -> password.length <= PASSWORD_MAX_LENGTH
        UPPERCASE -> password.any { it in 'A'..'Z' }
        LOWERCASE -> password.any { it in 'a'..'z' }
        DIGIT -> password.any { it in '0'..'9' }
        SPECIAL_CHARACTER -> password.any { it in PASSWORD_SPECIAL_CHARACTERS }
        NO_PERSONAL_INFORMATION -> identity.forbiddenFragments.none { password.contains(it, ignoreCase = true) }
    }
}

data class PasswordCheck(
    val requirement: PasswordRequirement,
    val isSatisfied: Boolean,
)

fun firstForbiddenPasswordCharacter(password: String): Char? =
    password.firstOrNull { !it.isAllowedInPassword() }

fun validatePassword(password: String, identity: PasswordIdentity = PasswordIdentity()): PasswordRequirement? =
    PasswordRequirement.entries.firstOrNull { !it.isSatisfiedBy(password, identity) }

fun passwordChecklist(password: String): List<PasswordCheck> =
    PasswordRequirement.entries
        .filter { it.isShownInChecklist }
        .map { PasswordCheck(it, it.isSatisfiedBy(password)) }

private fun Char.isAllowedInPassword(): Boolean =
    this in 'A'..'Z' || this in 'a'..'z' || this in '0'..'9' || this in PASSWORD_SPECIAL_CHARACTERS
