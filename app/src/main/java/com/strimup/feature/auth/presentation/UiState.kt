package com.strimup.feature.auth.presentation

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.common.user.domain.entity.UserEntity

data class UiState(
    val loginResultEntity: LoginResultEntity? = null,
    val user: UserEntity? = null,
    val loading: Boolean = false,
)
