package com.strimup.feature.auth.data.mapper

import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.data.response.UserRegisteredResponse
import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun UserRegisteredResponse.toEntity(): LoginResultEntity {
    val userRegistered = this.userRegistered
    return LoginResultEntity(
        message = "",
        token = this.token,
        user = UserEntity(
            id = userRegistered.id,
            userName = userRegistered.userName,
            email = userRegistered.email,
            role = UserRole.fromApi(userRegistered.role),
            avatarUrl = null
        )
    )
}
