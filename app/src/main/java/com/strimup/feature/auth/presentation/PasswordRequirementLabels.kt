package com.strimup.feature.auth.presentation

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.feature.auth.domain.PasswordRequirement

@StringRes
fun PasswordRequirement.toErrorMessageRes(): Int = when (this) {
    PasswordRequirement.MIN_LENGTH -> R.string.password_error_min_length
    PasswordRequirement.UPPERCASE -> R.string.password_error_uppercase
    PasswordRequirement.LOWERCASE -> R.string.password_error_lowercase
    PasswordRequirement.DIGIT -> R.string.password_error_digit
    PasswordRequirement.SPECIAL_CHARACTER -> R.string.password_error_special_character
}

@StringRes
fun PasswordRequirement.toChecklistLabelRes(): Int = when (this) {
    PasswordRequirement.MIN_LENGTH -> R.string.password_checklist_min_length
    PasswordRequirement.UPPERCASE -> R.string.password_checklist_uppercase
    PasswordRequirement.LOWERCASE -> R.string.password_checklist_lowercase
    PasswordRequirement.DIGIT -> R.string.password_checklist_digit
    PasswordRequirement.SPECIAL_CHARACTER -> R.string.password_checklist_special_character
}
