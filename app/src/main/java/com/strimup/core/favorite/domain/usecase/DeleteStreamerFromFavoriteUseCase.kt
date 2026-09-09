package com.strimup.core.favorite.domain.usecase

fun interface DeleteStreamerFromFavoriteUseCase {
    suspend operator fun invoke(id: String): Result<Unit>
}