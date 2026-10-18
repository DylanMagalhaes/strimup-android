package com.strimup.feature.auth.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.core.ui.text.UiText
import com.strimup.feature.auth.R
import com.strimup.feature.auth.domain.PasswordRequirement
import org.junit.Test

class PasswordRequirementLabelsTest {

    @Test
    fun `forbidden character error should name the character`() {
        val uiText = PasswordRequirement.ALLOWED_CHARACTERS.toErrorUiText("Motdepasse#2026")

        assertThat(uiText).isEqualTo(UiText.Resource(R.string.password_error_forbidden_character, listOf("#")))
    }

    @Test
    fun `forbidden whitespace should get a dedicated message`() {
        val uiText = PasswordRequirement.ALLOWED_CHARACTERS.toErrorUiText("Mot de passe!2026")

        assertThat(uiText).isEqualTo(UiText.Resource(R.string.password_error_whitespace))
    }
}
