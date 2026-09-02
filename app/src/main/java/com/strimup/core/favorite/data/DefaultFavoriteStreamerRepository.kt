package com.strimup.core.favorite.data

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.favorite.data.local.dao.FavoriteDao
import com.strimup.core.favorite.data.mapper.toDomain
import com.strimup.core.favorite.data.mapper.toRoomEntity
import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.streamer.data.mapper.toFavoriteRoom
import com.strimup.core.streamer.domain.repository.StreamerRepository
import javax.inject.Inject

class DefaultFavoriteStreamerRepository @Inject constructor(
    private val service: FavoriteApiService,
    private val streamerRepository: StreamerRepository,
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

            val streamerResult = streamerRepository.getStreamerById(id)
            val streamer = streamerResult.getOrThrow()

            favoriteDao.insertFavoriteStreamer(streamer.toFavoriteRoom())
        }
    }

    override suspend fun deleteFavoriteStreamer(id: String): Result<Unit> {
        return runCatching {
            service.deleteFavoriteStreamer(id)
            // Supprime de Room
            favoriteDao.deleteFavoriteStreamer(id)
        }
    }

}