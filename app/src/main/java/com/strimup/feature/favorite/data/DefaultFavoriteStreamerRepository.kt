package com.strimup.feature.favorite.data

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.favorite.data.mapper.toDomain
import com.strimup.feature.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class DefaultFavoriteStreamerRepository @Inject constructor(
    private val service: FavoriteApiService
) : FavoriteStreamerRepository {
    override suspend fun getFavoriteStreamers(): Result<List<Streamer>> {
        return runCatching {
            service.getFavoriteStreamers().map {
                it.toDomain()
            }
        }
    }

    override suspend fun addFavoriteStreamer(id: String): Result<Unit> {
        return runCatching {
            service.addFavoriteStreamer(id)
        }
    }

    override suspend fun deleteFavoriteStreamer(id: String): Result<Unit> {
        return runCatching {
            service.deleteFavoriteStreamer(id)
        }
    }

}