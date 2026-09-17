package com.strimup.feature.auth.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PasswordRequirementTest {

    @Test
    fun `validatePassword should return MIN_LENGTH when password is too short`() {
        assertThat(validatePassword("Aa1!aa")).isEqualTo(PasswordRequirement.MIN_LENGTH)
    }

    @Test
    fun `validatePassword should return UPPERCASE when password has no uppercase letter`() {
        assertThat(validatePassword("password123!")).isEqualTo(PasswordRequirement.UPPERCASE)
    }

    @Test
    fun `validatePassword should return LOWERCASE when password has no lowercase letter`() {
        assertThat(validatePassword("PASSWORD123!")).isEqualTo(PasswordRequirement.LOWERCASE)
    }

    @Test
    fun `validatePassword should return DIGIT when password has no digit`() {
        assertThat(validatePassword("Password!!!!")).isEqualTo(PasswordRequirement.DIGIT)
    }

    @Test
    fun `validatePassword should return SPECIAL_CHARACTER when password has no special character`() {
        assertThat(validatePassword("Password1234")).isEqualTo(PasswordRequirement.SPECIAL_CHARACTER)
    }

    @Test
    fun `validatePassword should return null when every requirement is satisfied`() {
        assertThat(validatePassword("Password123!")).isNull()
    }

    @Test
    fun `passwordChecklist should return every requirement with its satisfaction status`() {
        // GIVEN
        val checklist = passwordChecklist("Password123!")

        // THEN
        assertThat(checklist).hasSize(PasswordRequirement.entries.size)
        assertThat(checklist.map { it.requirement }).isEqualTo(PasswordRequirement.entries)
        assertThat(checklist.all { it.isSatisfied }).isTrue()
    }

    @Test
    fun `passwordChecklist should mark only the satisfied requirements as satisfied`() {
        // GIVEN
        val checklist = passwordChecklist("password")

        // THEN
        val satisfied = checklist.filter { it.isSatisfied }.map { it.requirement }
        assertThat(satisfied).containsExactly(PasswordRequirement.LOWERCASE)
    }
}
