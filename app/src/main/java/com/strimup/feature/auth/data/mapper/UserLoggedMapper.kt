package com.strimup.feature.auth.data.mapper

import com.strimup.feature.auth.data.response.UserLoggedResponse
import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.UserEntity
import com.strimup.feature.auth.domain.entity.UserRole

fun UserLoggedResponse.toEntity(): LoginResultEntity {
    val userLogged = this.userLogged
    return LoginResultEntity(
        message = this.message,
        user = UserEntity(
            id = userLogged.id,
            token = this.token,
            userName = userLogged.userName,
            email = userLogged.email,
            role = UserRole.valueOf(userLogged.role.uppercase()),
            birthDate = userLogged.birthDate,
            gender = userLogged.gender,
        )
    )
}