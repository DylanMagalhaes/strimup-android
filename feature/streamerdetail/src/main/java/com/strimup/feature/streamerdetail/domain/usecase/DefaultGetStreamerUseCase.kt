package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class DefaultGetStreamerUseCase @Inject constructor(
    private val streamerRepository: StreamerRepository,
    private val favoriteRepository: FavoriteStreamerRepository
) : GetStreamerUseCase {

    override suspend operator fun invoke(id: String): Result<StreamerDetailResult> {
        val streamerResult = streamerRepository.getStreamerById(id)

        if (streamerResult.isFailure) {
            return Result.failure(
                streamerResult.exceptionOrNull() ?: Exception("Erreur streamer")
            )
        }

        val streamer = streamerResult.getOrThrow()

        val favorites = favoriteRepository.observeFavorites().first()
        val isFavorite = favorites.any { it.id == id }

        return Result.success(
            StreamerDetailResult(
                streamer = streamer,
                isFavorite = isFavorite
            )
        )
    }
}