package com.strimup.feature.push.domain.usecase

fun interface RegisterRefreshedPushTokenUseCase {
    suspend operator fun invoke(token: String): Result<Unit>
}
