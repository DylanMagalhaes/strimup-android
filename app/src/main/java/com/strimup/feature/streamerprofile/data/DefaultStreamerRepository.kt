package com.strimup.feature.streamerprofile.data

import com.strimup.feature.streamerprofile.data.mapper.toEntity
import com.strimup.feature.streamerprofile.data.mapper.toRequest
import com.strimup.feature.streamerprofile.data.request.UpdateProfileRequest
import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import javax.inject.Inject

data class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService
): StreamerRepository {
    override suspend fun getStreamerById(id: String): Result<StreamerProfileEntity> {
        return runCatching {
            service.getStreamerById(id).toEntity()
        }
    }

    override suspend fun updateProfile(req: StreamerProfileEntity): Result<StreamerProfileEntity> {
        return runCatching {
            val request = req.toRequest()
            service.updateProfile(request).toEntity()
        }
    }
}
