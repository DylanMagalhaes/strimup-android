package com.strimup.feature.auth.domain.entity

import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole

data class RegisterCredentials(
    val userName: String,
    val email: String,
    val password: String,
    val birthDate: String,
    val gender: Gender,
    val role: UserRole,
)
