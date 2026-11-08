package com.strimup.feature.cuts.domain

import kotlinx.coroutines.flow.Flow

interface CutsDiscoveryRepository {
    val hasOpenedCuts: Flow<Boolean>

    suspend fun markCutsOpened()
}
