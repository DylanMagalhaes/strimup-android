package com.strimup.feature.favorite.presentation

import com.strimup.core.streamer.domain.entity.Streamer

data class FavoriteStreamersUiState(
    val isLoading: Boolean = false,
    val favoriteStreamers: List<Streamer> = emptyList(),
    val searchQuery: String = ""
) {
    val filteredStreamers: List<Streamer>
        get() = if (searchQuery.isBlank()) {
            favoriteStreamers
        } else {
            favoriteStreamers.filter {
                it.userName.contains(searchQuery, ignoreCase = true)
            }
        }

    val isEmpty: Boolean
        get() = !isLoading && favoriteStreamers.isEmpty()
}