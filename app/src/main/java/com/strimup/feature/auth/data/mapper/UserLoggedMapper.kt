package com.strimup.feature.auth.data.mapper

import com.strimup.feature.auth.data.response.UserLoggedResponse
import com.strimup.feature.auth.domain.entity.UserLoggedEntity

fun UserLoggedResponse.toEntity(): UserLoggedEntity {
    return UserLoggedEntity(
        id = this.userLogged.id,
        userName = this.userLogged.userName,
        gender = this.userLogged.gender,
        birthDate = this.userLogged.birthDate,
        email = this.userLogged.email,
        role = this.userLogged.role,
    )
}