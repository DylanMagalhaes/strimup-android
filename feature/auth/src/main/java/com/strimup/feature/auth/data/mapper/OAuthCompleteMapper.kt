package com.strimup.feature.auth.data.mapper

import com.strimup.feature.auth.data.response.OAuthCompleteResponse
import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun OAuthCompleteResponse.toEntity(): LoginResultEntity {
    return LoginResultEntity(
        message = message,
        token = token,
        user = user.toUserEntity()
    )
}
