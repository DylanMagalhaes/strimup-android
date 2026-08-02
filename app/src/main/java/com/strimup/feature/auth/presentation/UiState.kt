package com.strimup.feature.auth.presentation

import com.strimup.common.user.domain.entity.UserEntity
import com.strimup.feature.auth.domain.entity.LoginResultEntity

data class UiState(
    val loginResultEntity: LoginResultEntity? = null,
    val user: UserEntity? = null,
    val loading: Boolean = false,
)
