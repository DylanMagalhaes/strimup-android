package com.strimup.feature.auth.domain

private const val PASSWORD_MIN_LENGTH = 12
private val PASSWORD_SPECIAL_CHARACTER_REGEX = Regex("[@$!%*?&./\\-_]")

enum class PasswordRequirement {
    MIN_LENGTH,
    UPPERCASE,
    LOWERCASE,
    DIGIT,
    SPECIAL_CHARACTER;

    fun isSatisfiedBy(password: String): Boolean = when (this) {
        MIN_LENGTH -> password.length >= PASSWORD_MIN_LENGTH
        UPPERCASE -> password.any { it.isUpperCase() }
        LOWERCASE -> password.any { it.isLowerCase() }
        DIGIT -> password.any { it.isDigit() }
        SPECIAL_CHARACTER -> PASSWORD_SPECIAL_CHARACTER_REGEX.containsMatchIn(password)
    }
}

data class PasswordCheck(
    val requirement: PasswordRequirement,
    val isSatisfied: Boolean,
)

/**
 * Returns the first unmet [PasswordRequirement], or null if [password] satisfies all of them.
 */
fun validatePassword(password: String): PasswordRequirement? =
    PasswordRequirement.entries.firstOrNull { !it.isSatisfiedBy(password) }

/**
 * Returns every [PasswordRequirement] alongside whether [password] currently satisfies it,
 * for a live checklist UI.
 */
fun passwordChecklist(password: String): List<PasswordCheck> =
    PasswordRequirement.entries.map { PasswordCheck(it, it.isSatisfiedBy(password)) }
