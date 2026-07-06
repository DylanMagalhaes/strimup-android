package com.strimup.feature.home.data

import com.strimup.feature.home.data.mapper.toEntity
import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject
import okio.IOException

class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService,
) : StreamerRepository {

    override suspend fun getStreamers(
        filter: FilterEntity,
        favoriteStreamerIds: List<String>
    ): Result<List<StreamerEntity>> {
        return when (filter) {
            FilterEntity.Discovery -> {
                runCatching {
                    service.getRandomStreamers()
                        .items
                        ?.map { it.toEntity(isFavorite = favoriteStreamerIds.contains(it.id)) }
                        ?: throw IOException("error fetching random streamers")
                }
            }

            FilterEntity.Live -> {
                runCatching {
                    service.getInliveStreamers()
                        .items
                        ?.map { it.toEntity(isFavorite = favoriteStreamerIds.contains(it.id)) }
                        ?: throw IOException("error fetching random streamers")
                }
            }
        }
    }

    override suspend fun getFavoriteStreamerIds(): List<String> {
        return service.getFavoriteStreamers()
            .map { requireNotNull(it.streamerId) }
    }

    override suspend fun searchStreamers(query: String): Result<List<StreamerEntity>> {
        return runCatching {
            val response =service.searchStreamers(query)

            response.map { dataStreamer ->
                dataStreamer.toEntity()
            }
        }
    }


}

