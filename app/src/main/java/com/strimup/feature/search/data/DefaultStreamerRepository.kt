package com.strimup.feature.search.data

import com.strimup.feature.search.data.mapper.toEntity
import com.strimup.feature.search.domain.StreamerRepository
import com.strimup.feature.search.domain.entity.StreamerEntity
import javax.inject.Inject

class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService
): StreamerRepository {
    override suspend fun getStreamers(userName: String): Result<List<StreamerEntity>> {
        return runCatching {
            service.searchStreamers(userName).map{ response ->
                response.toEntity()
            }
        }
    }
}