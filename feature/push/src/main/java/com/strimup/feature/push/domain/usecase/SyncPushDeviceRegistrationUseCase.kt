package com.strimup.feature.push.domain.usecase

fun interface SyncPushDeviceRegistrationUseCase {
    suspend operator fun invoke()
}
