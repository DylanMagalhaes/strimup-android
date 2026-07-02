package com.strimup.feature.auth.data.mapper

import com.strimup.feature.auth.data.response.UserMeResponse
import com.strimup.feature.auth.domain.entity.UserEntity
import com.strimup.feature.auth.domain.entity.UserRole

fun UserMeResponse.UserMeData.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        userName = this.userName,
        email = this.email,
        role = UserRole.valueOf(this.role),
    )
}
