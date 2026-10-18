package com.strimup.feature.auth.data.mapper

import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.data.response.UserLoggedResponse
import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun UserLoggedResponse.toEntity(): LoginResultEntity {
    val userLogged = this.userLogged
    return LoginResultEntity(
        message = this.message ?: "",
        token = this.token,
        user = UserEntity(
            id = userLogged.id,
            userName = userLogged.userName,
            email = userLogged.email,
            role = UserRole.fromApi(userLogged.role),
            avatarUrl = this.userLogged.avatarUrl
            )
    )
}
