package com.strimup.core.favorite.domain

import com.strimup.core.streamer.domain.entity.Streamer
import kotlinx.coroutines.flow.Flow

interface FavoriteStreamerRepository {
    fun observeFavorites(): Flow<List<Streamer>>

    suspend fun refreshFavoriteStreamers(): Result<Unit>

    suspend fun addFavoriteStreamer(id: String): Result<Unit>

    suspend fun deleteFavoriteStreamer(id: String): Result<Unit>
}