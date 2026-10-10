package com.strimup.core.favorite.domain.usecase

fun interface ToggleFavoriteStreamerUseCase {
    suspend operator fun invoke(streamerId: String, isFavorite: Boolean): Result<Unit>
}
