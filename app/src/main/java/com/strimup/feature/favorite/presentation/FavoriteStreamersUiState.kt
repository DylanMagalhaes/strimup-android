package com.strimup.feature.favorite.presentation

import com.strimup.core.streamer.domain.entity.Streamer

data class FavoriteStreamersUiState(
    val isLoading: Boolean = true,
    val favoriteStreamers: List<Streamer> = emptyList()
) {
    val isEmpty: Boolean get() = !isLoading && favoriteStreamers.isEmpty()
}