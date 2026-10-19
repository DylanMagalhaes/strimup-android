package com.strimup.feature.search.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.search.domain.RecentStreamerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultObserveRecentStreamersUseCase @Inject constructor(
    private val repository: RecentStreamerRepository,
) : ObserveRecentStreamersUseCase {
    override fun invoke(): Flow<List<Streamer>> = repository.observeRecentStreamers()
}
