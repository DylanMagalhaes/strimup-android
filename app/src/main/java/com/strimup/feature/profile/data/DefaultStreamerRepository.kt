package com.strimup.feature.profile.data

import com.strimup.feature.profile.data.mapper.toEntity
import com.strimup.feature.profile.domain.StreamerRepository
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity
import javax.inject.Inject

class DefaultStreamerRepository @Inject constructor(
    val service: StreamerApiService
): StreamerRepository {
    override suspend fun getStreamerById(id: String): Result<ProfileStreamerEntity> {

        return runCatching {
            service.getStreamerById(id).toEntity()
        }

    }

}