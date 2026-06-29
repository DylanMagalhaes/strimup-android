package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.UserEntity

fun interface LoginUsecase {
    suspend operator fun invoke(email: String, password: String): Result<LoginResultEntity>
}