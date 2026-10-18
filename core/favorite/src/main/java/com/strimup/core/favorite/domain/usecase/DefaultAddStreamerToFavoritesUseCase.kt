package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class DefaultAddStreamerToFavoritesUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) : AddStreamerToFavoritesUseCase {
    override suspend fun invoke(id: String): Result<Unit> {
        return repository.addFavoriteStreamer(id)
    }

}
