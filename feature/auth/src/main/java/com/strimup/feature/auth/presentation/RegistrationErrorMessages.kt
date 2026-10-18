package com.strimup.feature.auth.presentation

import com.strimup.core.ui.text.UiText
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.R
import com.strimup.feature.auth.domain.BirthDateError
import com.strimup.feature.auth.domain.PseudoError
import com.strimup.feature.auth.domain.minimumAge

fun PseudoError.toUiText(): UiText = when (this) {
    PseudoError.REQUIRED -> UiText.Resource(R.string.pseudo_error_required)
    PseudoError.RESERVED -> UiText.Resource(R.string.pseudo_error_reserved)
}

fun BirthDateError.toUiText(role: UserRole?): UiText = when (this) {
    BirthDateError.REQUIRED -> UiText.Resource(R.string.birth_date_error_required)
    BirthDateError.INVALID -> UiText.Resource(R.string.birth_date_error_invalid)
    BirthDateError.IN_FUTURE -> UiText.Resource(R.string.birth_date_error_in_future)
    BirthDateError.TOO_YOUNG -> UiText.Resource(
        R.string.birth_date_error_too_young,
        listOf((role ?: UserRole.VIEWER).minimumAge),
    )
}
