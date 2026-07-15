package com.strimup.common.user.data.mapper

import com.strimup.common.user.data.local.model.UserRoomEntity
import com.strimup.common.user.data.response.UserMeResponse
import com.strimup.common.user.domain.entity.UserEntity
import com.strimup.common.user.domain.entity.UserRole

fun UserMeResponse.UserMeData.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = UserRole.valueOf(this.role),
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