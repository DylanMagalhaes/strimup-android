package com.strimup.feature.auth.domain.entity

import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole

data class OAuthCredentials(
    val tmp: String,
    val role: UserRole,
    val birthDate: String,
    val gender: Gender,
)
