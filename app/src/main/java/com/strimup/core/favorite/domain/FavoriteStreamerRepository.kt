package com.strimup.core.favorite.domain

import com.strimup.core.streamer.domain.entity.Streamer

interface FavoriteStreamerRepository {
    suspend fun getFavoriteStreamers(): Result<List<Streamer>>

    suspend fun addFavoriteStreamer(id: String): Result<Unit>

    suspend fun deleteFavoriteStreamer(id: String): Result<Unit>
}