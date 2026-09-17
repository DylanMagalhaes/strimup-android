package com.strimup.core.ui.user

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.core.user.domain.entity.Gender

@StringRes
fun Gender.toLabelRes(): Int = when (this) {
    Gender.MALE -> R.string.gender_male
    Gender.FEMALE -> R.string.gender_female
    Gender.NON_BINARY -> R.string.gender_non_binary
    Gender.PREFER_NOT_TO_SAY -> R.string.gender_prefer_not_to_say
}
