package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class AddStreamerToFavoritesUseCase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.addFavoriteStreamer(id)
    }
}
