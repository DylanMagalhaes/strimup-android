package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.entity.LoginResultEntity

fun interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<LoginResultEntity>
}