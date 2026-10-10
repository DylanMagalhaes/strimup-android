package com.strimup.feature.auth.domain

import com.strimup.core.user.domain.entity.UserRole
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException

private val RESERVED_PSEUDOS = setOf("streamer", "viewer", "entreprise", "admin")
private const val DELETED_ACCOUNT_PSEUDO_PREFIX = "compte-supprime"
private const val VIEWER_MINIMUM_AGE = 13
private const val STREAMER_MINIMUM_AGE = 16

enum class PseudoError { REQUIRED, RESERVED }

enum class BirthDateError { REQUIRED, INVALID, IN_FUTURE, TOO_YOUNG }

fun validatePseudo(pseudo: String): PseudoError? {
    val normalizedPseudo = pseudo.trim().lowercase()
    return when {
        normalizedPseudo.isEmpty() -> PseudoError.REQUIRED
        normalizedPseudo in RESERVED_PSEUDOS -> PseudoError.RESERVED
        normalizedPseudo.startsWith(DELETED_ACCOUNT_PSEUDO_PREFIX) -> PseudoError.RESERVED
        else -> null
    }
}

val UserRole.minimumAge: Int
    get() = when (this) {
        UserRole.STREAMER -> STREAMER_MINIMUM_AGE
        UserRole.VIEWER,
        UserRole.ADMIN -> VIEWER_MINIMUM_AGE
    }

fun validateBirthDate(birthDate: String, role: UserRole?, today: LocalDate): BirthDateError? {
    val date = birthDate.toLocalDateOrNull()

    return when {
        birthDate.isBlank() -> BirthDateError.REQUIRED
        date == null -> BirthDateError.INVALID
        date.isAfter(today) -> BirthDateError.IN_FUTURE
        role != null && Period.between(date, today).years < role.minimumAge -> BirthDateError.TOO_YOUNG
        else -> null
    }
}

private fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this)
} catch (_: DateTimeParseException) {
    null
}
