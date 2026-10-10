package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.domain.entity.Streamer
import javax.inject.Inject

class DefaultRefreshFavoriteStreamersUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) : RefreshFavoriteStreamerUseCase {
    override suspend fun invoke(): Result<Unit> {
        return repository.refreshFavoriteStreamers()
    }

}