package com.strimup.core.user.data.mapper

import com.strimup.core.database.model.UserRoomEntity
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole

fun UserEntity.toRoomEntity(): UserRoomEntity {
    return UserRoomEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = this.role.name,
        imageUrl = this.avatarUrl
        )
}

fun UserRoomEntity.toDomainEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = UserRole.fromApi(this.role),
        avatarUrl = this.imageUrl
    )
}