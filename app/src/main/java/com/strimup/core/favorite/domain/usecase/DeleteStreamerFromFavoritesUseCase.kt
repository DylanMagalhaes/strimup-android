package com.strimup.core.favorite.domain.usecase

fun interface DeleteStreamerFromFavoritesUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}