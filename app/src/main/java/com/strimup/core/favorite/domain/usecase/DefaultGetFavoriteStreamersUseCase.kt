package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.domain.entity.Streamer
import javax.inject.Inject

class DefaultGetFavoriteStreamersUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository
): GetFavoriteStreamerUseCase {
    override suspend fun invoke(): Result<List<Streamer>> {
        return repository.getFavoriteStreamers()
    }

}