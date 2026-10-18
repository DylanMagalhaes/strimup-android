package com.strimup.core.favorite.domain.usecase

fun interface RefreshFavoriteStreamerUseCase {

    suspend operator fun invoke(): Result<Unit>
}