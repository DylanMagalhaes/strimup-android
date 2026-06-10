package com.strimup.feature.home.data

import com.strimup.feature.home.data.mapper.toEntity
import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject
import okio.IOException

class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService,
) : StreamerRepository {

    override suspend fun getRandomStreamers(): List<StreamerEntity> {
        return service.getRandomStreamers()
            .items
            ?.map { it.toEntity() }
            ?: throw IOException("error fetching random streamers")
    }

    override suspend fun getInLiveStreamers(): List<StreamerEntity> {
        return service.getInliveStreamers()
            .items
            ?.map { it.toEntity() }
            ?: throw IOException("error fetching random streamers")
    }


}

