package com.strimup.feature.auth.presentation

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.core.ui.text.UiText
import com.strimup.feature.auth.domain.PasswordRequirement
import com.strimup.feature.auth.domain.firstForbiddenPasswordCharacter

fun PasswordRequirement.toErrorUiText(password: String): UiText = when (this) {
    PasswordRequirement.ALLOWED_CHARACTERS -> forbiddenCharacterUiText(password)
    PasswordRequirement.MIN_LENGTH -> UiText.Resource(R.string.password_error_min_length)
    PasswordRequirement.MAX_LENGTH -> UiText.Resource(R.string.password_error_max_length)
    PasswordRequirement.UPPERCASE -> UiText.Resource(R.string.password_error_uppercase)
    PasswordRequirement.LOWERCASE -> UiText.Resource(R.string.password_error_lowercase)
    PasswordRequirement.DIGIT -> UiText.Resource(R.string.password_error_digit)
    PasswordRequirement.SPECIAL_CHARACTER -> UiText.Resource(R.string.password_error_special_character)
    PasswordRequirement.NO_PERSONAL_INFORMATION -> UiText.Resource(R.string.password_error_personal_information)
}

@StringRes
fun PasswordRequirement.toChecklistLabelRes(): Int = when (this) {
    PasswordRequirement.ALLOWED_CHARACTERS -> R.string.password_checklist_allowed_characters
    PasswordRequirement.MIN_LENGTH -> R.string.password_checklist_min_length
    PasswordRequirement.MAX_LENGTH -> R.string.password_checklist_max_length
    PasswordRequirement.UPPERCASE -> R.string.password_checklist_uppercase
    PasswordRequirement.LOWERCASE -> R.string.password_checklist_lowercase
    PasswordRequirement.DIGIT -> R.string.password_checklist_digit
    PasswordRequirement.SPECIAL_CHARACTER -> R.string.password_checklist_special_character
    PasswordRequirement.NO_PERSONAL_INFORMATION -> R.string.password_checklist_personal_information
}

private fun forbiddenCharacterUiText(password: String): UiText {
    val forbiddenCharacter = firstForbiddenPasswordCharacter(password)
        ?: return UiText.Resource(R.string.password_error_special_character)

    return if (forbiddenCharacter.isWhitespace()) {
        UiText.Resource(R.string.password_error_whitespace)
    } else {
        UiText.Resource(R.string.password_error_forbidden_character, listOf(forbiddenCharacter.toString()))
    }
}
