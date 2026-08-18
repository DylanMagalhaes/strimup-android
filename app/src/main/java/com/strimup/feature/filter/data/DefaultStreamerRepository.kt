package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.mapper.toDomain
import com.strimup.feature.filter.data.mapper.toDto
import com.strimup.feature.filter.data.request.StreamerMatchRequest
import com.strimup.feature.filter.data.response.StreamerMatchResponse
import com.strimup.feature.filter.domain.StreamerRepository
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.StreamerMatchResult
import javax.inject.Inject

class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService
): StreamerRepository {
    override suspend fun getStreamersByFilter(
        page: Int,
        filter: FilterCriteria
    ): Result<StreamerMatchResult> {
        return runCatching {
            val request = StreamerMatchRequest(
                page = page,
                limit = 20,
                filter = filter.toDto()
            )

           val result =  service.getFilteredStreamers(request)

            StreamerMatchResult(
                total = result.total,
                streamers = result.matchedStreamers.map {
                    it.toDomain()
                }
            )
        }
    }
}