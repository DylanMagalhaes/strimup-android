package com.strimup.feature.cuts.domain.usecase

import com.strimup.feature.cuts.domain.CutsDiscoveryRepository
import javax.inject.Inject

class DefaultMarkCutsOpenedUseCase @Inject constructor(
    private val repository: CutsDiscoveryRepository,
) : MarkCutsOpenedUseCase {
    override suspend fun invoke() = repository.markCutsOpened()
}
