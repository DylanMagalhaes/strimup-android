package com.strimup.presentation

import com.strimup.common.user.domain.entity.UserEntity

data class Uistate(
    val user: UserEntity? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null
)
