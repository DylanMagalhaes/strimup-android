package com.strimup.feature.push.domain.usecase

fun interface RegisterPushDeviceUseCase {
    suspend operator fun invoke(token: String?): Result<Unit>
}
