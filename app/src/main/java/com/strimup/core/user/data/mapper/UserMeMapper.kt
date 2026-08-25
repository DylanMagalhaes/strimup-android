package com.strimup.core.user.data.mapper

import com.strimup.core.user.data.local.model.UserRoomEntity
import com.strimup.core.user.data.response.UserMeResponse
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole

fun UserMeResponse.UserMeData.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = UserRole.valueOf(this.role),
        avatarUrl = this.imageUrl
    )
}

fun UserEntity.toRoomEntity(): UserRoomEntity {
    return UserRoomEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = this.role.name,
        avatarUrl = this.avatarUrl
        )
}

fun UserRoomEntity.toDomainEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = UserRole.valueOf(this.role),
        avatarUrl = this.avatarUrl
    )
}