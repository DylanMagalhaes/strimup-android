package com.strimup.feature.auth.data.mapper

import com.strimup.common.user.data.local.model.UserRoomEntity
import com.strimup.common.user.domain.entity.UserEntity
import com.strimup.common.user.domain.entity.UserRole
import com.strimup.feature.auth.data.response.UserLoggedResponse
import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun UserLoggedResponse.toEntity(): LoginResultEntity {
    val userLogged = this.userLogged
    return LoginResultEntity(
        message = this.message,
        token = this.token,
        user = UserEntity(
            id = userLogged.id,
            userName = userLogged.userName,
            email = userLogged.email,
            role = UserRole.valueOf(userLogged.role.uppercase()),

        )
    )
}

fun UserEntity.toRoomEntity(): UserRoomEntity {
    return UserRoomEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = this.role.name,
    )
}

fun UserRoomEntity.toDomainEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = UserRole.valueOf(this.role),
    )
}
