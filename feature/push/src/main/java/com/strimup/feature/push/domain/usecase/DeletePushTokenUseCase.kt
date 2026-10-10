package com.strimup.feature.push.domain.usecase

fun interface DeletePushTokenUseCase {
    suspend operator fun invoke(): Result<Unit>
}
