package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.CutsDiscoveryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultObserveHasOpenedCutsUseCase @Inject constructor(
    private val repository: CutsDiscoveryRepository,
) : ObserveHasOpenedCutsUseCase {
    override fun invoke(): Flow<Boolean> = repository.hasOpenedCuts
}
