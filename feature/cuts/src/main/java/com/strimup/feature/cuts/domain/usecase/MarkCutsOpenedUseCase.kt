package com.strimup.feature.cuts.domain.usecase

fun interface MarkCutsOpenedUseCase {
    suspend operator fun invoke()
}
