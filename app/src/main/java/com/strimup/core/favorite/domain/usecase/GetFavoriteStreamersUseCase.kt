package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.domain.entity.Streamer
import javax.inject.Inject

class GetFavoriteStreamersUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) {
    suspend operator fun invoke(): Result<List<Streamer>> {
        return repository.getFavoriteStreamers()
    }
}