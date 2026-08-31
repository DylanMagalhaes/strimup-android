package com.strimup.feature.favorite.domain.usecase

import com.strimup.feature.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class DeleteFavoriteStreamerUsecase @Inject constructor(
    private val repository: FavoriteStreamerRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteFavoriteStreamer(id)
    }
}