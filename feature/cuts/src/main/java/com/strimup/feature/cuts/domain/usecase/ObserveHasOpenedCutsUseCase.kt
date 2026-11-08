package com.strimup.feature.cuts.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface ObserveHasOpenedCutsUseCase {
    operator fun invoke(): Flow<Boolean>
}
