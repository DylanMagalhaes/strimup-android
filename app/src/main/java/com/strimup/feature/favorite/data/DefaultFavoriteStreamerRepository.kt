package com.strimup.feature.favorite.data

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.favorite.data.local.dao.FavoriteDao
import com.strimup.feature.favorite.data.mapper.toDomain
import com.strimup.feature.favorite.data.mapper.toRoomEntity
import com.strimup.feature.favorite.domain.FavoriteStreamerRepository
import javax.inject.Inject

class DefaultFavoriteStreamerRepository @Inject constructor(
    private val service: FavoriteApiService,
    private val favoriteDao: FavoriteDao
) : FavoriteStreamerRepository {
    override suspend fun getFavoriteStreamers(): Result<List<Streamer>> {
        return runCatching {
            val remoteFavorites = service.getFavoriteStreamers()

            val favoriteStreamers = remoteFavorites.map {
                it.toDomain()
            }

            remoteFavorites.forEach { favorite ->
                favoriteDao.insertFavoriteStreamer(favorite.toRoomEntity())
            }

            favoriteStreamers
        }.recoverCatching { exception ->
            val localFavorite = favoriteDao.getAllFavoritesOnce()

            if (localFavorite.isNotEmpty()) {
                localFavorite.map {
                    it.toDomain()
                }
            } else
                throw exception
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