package com.strimup.feature.auth.presentation

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.UserEntity

data class UiState(
    val loginResultEntity: LoginResultEntity? = null,
    val user: UserEntity? = null,
    val loading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val message: String? = null
)
