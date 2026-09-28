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
    fun `passwordChecklist should only list the requirements shown to the user`() {
        val checklist = passwordChecklist("Password123!")

        assertThat(checklist.map { it.requirement }).containsExactly(
            PasswordRequirement.MIN_LENGTH,
            PasswordRequirement.UPPERCASE,
            PasswordRequirement.LOWERCASE,
            PasswordRequirement.DIGIT,
            PasswordRequirement.SPECIAL_CHARACTER,
        ).inOrder()
        assertThat(checklist.all { it.isSatisfied }).isTrue()
    }

    @Test
    fun `passwordChecklist should mark only the satisfied requirements as satisfied`() {
        val checklist = passwordChecklist("password")

        val satisfied = checklist.filter { it.isSatisfied }.map { it.requirement }
        assertThat(satisfied).containsExactly(PasswordRequirement.LOWERCASE)
    }

    @Test
    fun `validatePassword should accept every allowed special character`() {
        PASSWORD_SPECIAL_CHARACTERS.forEach { specialCharacter ->
            assertThat(validatePassword("Motdepasse${specialCharacter}2026")).isNull()
        }
    }

    @Test
    fun `validatePassword should reject a special character outside the allowed list`() {
        listOf('#', '+', '€', '(', ')', ' ', 'é').forEach { forbiddenCharacter ->
            assertThat(validatePassword("Motdepasse!2026$forbiddenCharacter"))
                .isEqualTo(PasswordRequirement.ALLOWED_CHARACTERS)
        }
    }

    @Test
    fun `validatePassword should reject a password longer than 128 characters`() {
        val password = "Aa1!" + "a".repeat(125)

        assertThat(validatePassword(password)).isEqualTo(PasswordRequirement.MAX_LENGTH)
    }

    @Test
    fun `validatePassword should accept a password of exactly 128 characters`() {
        val password = "Aa1!" + "a".repeat(124)

        assertThat(validatePassword(password)).isNull()
    }

    @Test
    fun `validatePassword should reject a password containing the pseudo whatever the case`() {
        val identity = PasswordIdentity(pseudo = "Inox", email = "someone@test.com")

        assertThat(validatePassword("MonINOX!2026", identity))
            .isEqualTo(PasswordRequirement.NO_PERSONAL_INFORMATION)
    }

    @Test
    fun `validatePassword should reject a password containing the e-mail local part`() {
        val identity = PasswordIdentity(pseudo = "Inox", email = "dylan.m@test.com")

        assertThat(validatePassword("Dylan.m!20262026", identity))
            .isEqualTo(PasswordRequirement.NO_PERSONAL_INFORMATION)
    }

    @Test
    fun `validatePassword should ignore an empty identity`() {
        assertThat(validatePassword("Motdepasse!2026", PasswordIdentity())).isNull()
    }

    @Test
    fun `firstForbiddenPasswordCharacter should return the first forbidden character`() {
        assertThat(firstForbiddenPasswordCharacter("Mot#de+passe")).isEqualTo('#')
        assertThat(firstForbiddenPasswordCharacter("Motdepasse!2026")).isNull()
    }
}
