package com.strimup.core.streamer.data.repository

import android.content.Context
import android.net.Uri
import com.strimup.core.streamer.data.StreamerApiService
import com.strimup.core.streamer.data.mapper.toDomain
import com.strimup.core.streamer.data.mapper.toEntity
import com.strimup.core.streamer.data.mapper.toUpdateProfileRequest
import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.streamer.domain.repository.StreamerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class DefaultStreamerRepository @Inject constructor(
    private val service: StreamerApiService,
    @ApplicationContext private val context: Context
) : StreamerRepository {

    override suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> {
        return runCatching {
            service.getRandomStreamers()
                .items
                ?.map { it.toEntity(isFavorite = favoriteStreamerIds.contains(it.id)) }
                ?: throw IOException("error fetching random streamers")
        }
    }

    override suspend fun getLiveStreamers(favoriteStreamerIds: List<String>): Result<List<Streamer>> {
        return runCatching {
            service.getInliveStreamers()
                .items
                ?.map { it.toEntity(isFavorite = favoriteStreamerIds.contains(it.id)) }
                ?: throw IOException("error fetching live streamers")
        }
    }

    override suspend fun searchStreamers(userName: String): Result<List<Streamer>> {
        return runCatching {
            service.searchStreamers(userName).map { it.toEntity() }
        }
    }

    override suspend fun getStreamerById(id: String): Result<Streamer> {
        return runCatching {
            service.getStreamerById(id).toEntity()
        }
    }

    override suspend fun updateProfile(streamer: Streamer): Result<Streamer> {
        return runCatching {
            val request = streamer.toUpdateProfileRequest()
            service.updateProfile(request).streamer.toEntity()
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
            val bodyPart = MultipartBody.Part.createFormData(
                "avatar",
                "profile_avatar.jpg",
                requestBody
            )

            service.updateAvatar(bodyPart).toDomain()
        }
    }

    override suspend fun getStreamerOptions(): Result<StreamerOptions> {
        return runCatching {
            service.getStreamerOptions().toEntity()
        }
    }

    override suspend fun getStreamersByFilter(request: StreamerMatchRequest): Result<StreamerMatchResult> {
        return runCatching {
            service.getFilteredStreamers(request).toDomain()
        }
    }
}
