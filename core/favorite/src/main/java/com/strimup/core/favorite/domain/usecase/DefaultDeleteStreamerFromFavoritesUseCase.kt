package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class DefaultDeleteStreamerFromFavoritesUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) : DeleteStreamerFromFavoritesUseCase {
    override suspend fun invoke(id: String): Result<Unit> {
        return repository.deleteFavoriteStreamer(id)
    }

}