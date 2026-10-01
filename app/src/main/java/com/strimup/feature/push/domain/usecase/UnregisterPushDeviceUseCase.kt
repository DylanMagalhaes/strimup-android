package com.strimup.feature.push.domain.usecase

fun interface UnregisterPushDeviceUseCase {
    suspend operator fun invoke(): Result<Unit>
}
