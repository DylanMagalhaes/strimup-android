package com.strimup.feature.auth.domain

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserRole
import org.junit.Test
import java.time.LocalDate

class RegistrationRulesTest {

    private val today = LocalDate.of(2026, 9, 28)

    @Test
    fun `validatePseudo should require a pseudo`() {
        assertThat(validatePseudo("   ")).isEqualTo(PseudoError.REQUIRED)
    }

    @Test
    fun `validatePseudo should reject reserved pseudos whatever the case`() {
        listOf("streamer", "Viewer", "ENTREPRISE", " admin ").forEach { pseudo ->
            assertThat(validatePseudo(pseudo)).isEqualTo(PseudoError.RESERVED)
        }
    }

    @Test
    fun `validatePseudo should reject pseudos starting with compte-supprime`() {
        assertThat(validatePseudo("compte-supprime-42")).isEqualTo(PseudoError.RESERVED)
    }

    @Test
    fun `validatePseudo should accept a regular pseudo`() {
        assertThat(validatePseudo("Inox")).isNull()
        assertThat(validatePseudo("administrateur-du-fun")).isNull()
    }

    @Test
    fun `validateBirthDate should require a date`() {
        assertThat(validateBirthDate("", UserRole.VIEWER, today)).isEqualTo(BirthDateError.REQUIRED)
    }

    @Test
    fun `validateBirthDate should reject a date that is not ISO formatted`() {
        assertThat(validateBirthDate("31/01/2000", UserRole.VIEWER, today)).isEqualTo(BirthDateError.INVALID)
    }

    @Test
    fun `validateBirthDate should reject a date in the future`() {
        assertThat(validateBirthDate("2026-09-29", UserRole.VIEWER, today)).isEqualTo(BirthDateError.IN_FUTURE)
    }

    @Test
    fun `validateBirthDate should require 13 years for a viewer`() {
        assertThat(validateBirthDate("2013-09-29", UserRole.VIEWER, today)).isEqualTo(BirthDateError.TOO_YOUNG)
        assertThat(validateBirthDate("2013-09-28", UserRole.VIEWER, today)).isNull()
    }

    @Test
    fun `validateBirthDate should require 16 years for a streamer`() {
        assertThat(validateBirthDate("2010-09-29", UserRole.STREAMER, today)).isEqualTo(BirthDateError.TOO_YOUNG)
        assertThat(validateBirthDate("2010-09-28", UserRole.STREAMER, today)).isNull()
    }

    @Test
    fun `validateBirthDate should not check the age before a role is chosen`() {
        assertThat(validateBirthDate("2020-01-01", null, today)).isNull()
    }
}
