package com.strimup.feature.auth.data.mapper

import com.strimup.feature.auth.data.response.UserLoggedResponse
import com.strimup.feature.auth.domain.entity.UserEntity

fun UserLoggedResponse.toEntity(): UserEntity {
    return UserEntity(
        id = this.userLogged.id,
        userName = this.userLogged.userName,
        gender = this.userLogged.gender,
        birthDate = this.userLogged.birthDate,
        email = this.userLogged.email,
        role = this.userLogged.role,
    )
}