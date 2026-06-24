package com.strimup.feature.streamerdetail.data

import com.strimup.feature.streamerdetail.data.mapper.toEntity
import com.strimup.feature.streamerdetail.domain.StreamerRepository
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity
import javax.inject.Inject

class DefaultStreamerRepository @Inject constructor(
    val service: StreamerApiService
): StreamerRepository {
    override suspend fun getStreamerById(id: String): Result<StreamerDetailEntity> {

        return runCatching {
            service.getStreamerById(id).toEntity()
        }

    }

}