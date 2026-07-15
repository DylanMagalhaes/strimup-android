package com.strimup.feature.auth.domain.entity

import com.strimup.common.user.domain.entity.UserEntity

data class LoginResultEntity(
    val message: String,
    val user: UserEntity,
    val token: String,
)
