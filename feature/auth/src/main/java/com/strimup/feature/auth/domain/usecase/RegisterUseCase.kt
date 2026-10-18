package com.strimup.feature.auth.domain.usecase

import com.strimup.feature.auth.domain.entity.LoginResultEntity
import com.strimup.feature.auth.domain.entity.RegisterCredentials

fun interface RegisterUseCase {
    suspend operator fun invoke(credentials: RegisterCredentials): Result<LoginResultEntity>
}
