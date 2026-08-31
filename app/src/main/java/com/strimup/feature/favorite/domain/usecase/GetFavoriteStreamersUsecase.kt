package com.strimup.feature.favorite.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class GetFavoriteStreamersUsecase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) {
    suspend operator fun invoke(): Result<List<Streamer>> {
        return repository.getFavoriteStreamers()
    }
}