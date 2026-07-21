package com.strimup.feature.streamerprofile.data

import com.strimup.feature.streamerprofile.data.mapper.toEntity
import com.strimup.feature.streamerprofile.data.mapper.toRequest
import com.strimup.feature.streamerprofile.data.response.UpdateProfileResponse
import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import javax.inject.Inject

 class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService
) : StreamerRepository {
    override suspend fun getStreamerById(id: String): Result<StreamerProfileEntity> {
        return runCatching {
            service.getStreamerById(id).toEntity()
        }
    }

     override suspend fun updateProfile(profile: StreamerProfileEntity): Result<StreamerProfileEntity> {
         return runCatching {
             val request = profile.toRequest()

             val response = service.updateProfile(request)

             val entity = response.streamer.toEntity()
             entity
         }
     }

    override suspend fun getStreamerOptions(): Result<StreamerOptionsEntity> {
        return runCatching {
            service.getStreamerOptions().toEntity()
        }
    }
}
