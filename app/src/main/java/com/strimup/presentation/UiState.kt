package com.strimup.presentation

import com.strimup.core.user.domain.entity.UserEntity

data class UiState(
    val user: UserEntity? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null
)


