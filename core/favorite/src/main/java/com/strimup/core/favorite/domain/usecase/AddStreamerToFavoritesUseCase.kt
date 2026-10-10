package com.strimup.core.favorite.domain.usecase

fun interface AddStreamerToFavoritesUseCase {

    suspend operator fun invoke(id: String): Result<Unit>
}