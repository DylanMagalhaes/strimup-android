package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class DefaultToggleFavoriteStreamerUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository,
) : ToggleFavoriteStreamerUseCase {
    override suspend fun invoke(streamerId: String, isFavorite: Boolean): Result<Unit> =
        if (isFavorite) {
            repository.deleteFavoriteStreamer(streamerId)
        } else {
            repository.addFavoriteStreamer(streamerId)
        }
}
