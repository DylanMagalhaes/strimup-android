package com.strimup.feature.home.domain.usecase

fun interface RefreshBannerUseCase {
    suspend operator fun invoke(): Result<Unit>
}
