package com.strimup.feature.account.presentation.account

import com.strimup.core.user.domain.entity.UserEntity

data class AccountUiState(
    val user: UserEntity? = null,
    val isLoggingOut: Boolean = false,
)
