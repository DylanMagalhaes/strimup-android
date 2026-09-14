package com.strimup.core.favorite.data

import com.strimup.core.favorite.data.local.dao.FavoriteDao
import com.strimup.core.favorite.data.mapper.toDomain
import com.strimup.core.favorite.data.mapper.toRoomEntity
import com.strimup.core.favorite.domain.FavoriteStreamerRepository
import com.strimup.core.network.toDomainResult
import com.strimup.core.streamer.data.mapper.toFavoriteRoom
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.repository.StreamerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultFavoriteStreamerRepository @Inject constructor(
    private val service: FavoriteApiService,
    private val streamerRepository: StreamerRepository,
    private val favoriteDao: FavoriteDao
) : FavoriteStreamerRepository {
    override fun observeFavorites(): Flow<List<Streamer>> {

        return favoriteDao.observeFavorites()
            .map { entities ->
                entities.map { it.toDomain() }
            }

    }

    override suspend fun refreshFavoriteStreamers(): Result<Unit> {
        return runCatching {
            val remoteFavorites = service.getFavoriteStreamers()

            remoteFavorites.forEach { favorite ->
                favoriteDao.insertFavoriteStreamer(favorite.toRoomEntity())
            }
        }.toDomainResult()
    }

    override suspend fun addFavoriteStreamer(id: String): Result<Unit> {
        return runCatching {
            service.addFavoriteStreamer(id)

            val streamerResult = streamerRepository.getStreamerById(id)
            val streamer = streamerResult.getOrThrow()

            favoriteDao.insertFavoriteStreamer(streamer.toFavoriteRoom())
        }.toDomainResult()
    }

    override suspend fun deleteFavoriteStreamer(id: String): Result<Unit> {
        return runCatching {
            service.deleteFavoriteStreamer(id)
            // Supprime de Room
            favoriteDao.deleteFavoriteStreamer(id)
        }.toDomainResult()
    }

}