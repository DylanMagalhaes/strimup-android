package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultGetStreamerUsecase @Inject constructor(
    private val streamerRepository: StreamerRepository,
    private val favoriteRepository: FavoriteStreamerRepository
) : GetStreamerUsecase {

    override suspend operator fun invoke(id: String): Result<StreamerDetailResult> {
        val streamerResult = streamerRepository.getStreamerById(id)
        if (streamerResult.isFailure) {
            return Result.failure(streamerResult.exceptionOrNull() ?: Exception("Erreur streamer"))
        }
        val streamer = streamerResult.getOrThrow()

        val favoritesResult = favoriteRepository.getFavoriteStreamers()
        val isFavorite = favoritesResult
            .getOrDefault(emptyList())
            .any { it.id == id }

        return Result.success(
            StreamerDetailResult(
                streamer = streamer,
                isFavorite = isFavorite
            )
        )
    }
}