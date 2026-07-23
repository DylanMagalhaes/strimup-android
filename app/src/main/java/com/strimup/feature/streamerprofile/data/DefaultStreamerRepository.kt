package com.strimup.feature.streamerprofile.data

import android.content.Context
import android.net.Uri
import com.strimup.feature.streamerprofile.data.mapper.toDomain
import com.strimup.feature.streamerprofile.data.mapper.toEntity
import com.strimup.feature.streamerprofile.data.mapper.toRequest
import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService,
    @ApplicationContext private val context: Context
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

     override suspend fun updateAvatar(uri: String): Result<String> {
         return runCatching {
             val parsedUri = Uri.parse(uri)
             val contentResolver = context.contentResolver
             val mimeType = contentResolver.getType(parsedUri) ?: "image/jpeg"

             val bytes = contentResolver.openInputStream(parsedUri)?.use { it.readBytes() }
                 ?: throw IllegalArgumentException("Impossible de lire l'image")

             val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
             val bodyPart = MultipartBody.Part.createFormData("avatar", "profile_avatar.jpg", requestBody)

             service.updateAvatar(bodyPart).toDomain()
         }
     }
 }
