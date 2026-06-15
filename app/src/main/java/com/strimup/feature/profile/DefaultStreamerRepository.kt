package com.strimup.feature.profile

import com.strimup.feature.profile.data.StreamerApiService
import com.strimup.feature.profile.domain.StreamerRepository
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity
import com.strimup.feature.profile.data.mapper.toEntity
import javax.inject.Inject

class DefaultStreamerRepository @Inject constructor(
    val service: StreamerApiService
): StreamerRepository {
    override suspend fun getStreamerById(id: Int): ProfileStreamerEntity {
         return service.getStreamerById(id).toEntity()
    }
}