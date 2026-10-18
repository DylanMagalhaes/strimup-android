package com.strimup.feature.auth.data.mapper

import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.data.response.UserRegisteredResponse
import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun UserRegisteredResponse.UserRegistered.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        userName = userName,
        email = email,
        role = UserRole.fromApi(role),
        avatarUrl = null
    )
}

fun UserRegisteredResponse.toEntity(): LoginResultEntity {
    return LoginResultEntity(
        message = "",
        token = this.token,
        user = userRegistered.toUserEntity()
    )
}
