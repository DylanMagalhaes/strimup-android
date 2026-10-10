package com.strimup.feature.streamervideos.data

import com.strimup.core.common.DomainException
import com.strimup.core.network.toDomainError
import com.strimup.core.network.toDomainResult
import com.strimup.core.streamer.data.mapper.toEntity
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.data.file.VideoFileReader
import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideo
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import javax.inject.Inject

private const val VIDEO_PART = "video"
private const val TITLE_PART = "title"
private const val DESCRIPTION_PART = "description"
private const val UPLOAD_FILE_NAME = "video"

class DefaultStreamerVideoRepository @Inject constructor(
    private val service: StreamerVideoApiService,
    private val fileReader: VideoFileReader,
) : StreamerVideoRepository {

    override suspend fun getMyVideos(): Result<List<Streamer.Video>> {
        return runCatching {
            service.getMyVideos().sortedBy { it.order }.map { it.toEntity() }
        }.toDomainResult()
    }

    override suspend fun getLocalVideoFile(uri: String): Result<LocalVideoFile> {
        return runCatching { fileReader.describe(uri) }.toDomainResult()
    }

    override fun uploadVideo(video: NewStreamerVideo): Flow<VideoUploadEvent> = channelFlow {
        val body = StreamingVideoRequestBody(
            mediaType = video.format.mimeType.toMediaType(),
            sizeBytes = video.file.sizeBytes,
            openStream = { fileReader.open(video.file.uri) },
            onProgress = { percent -> trySend(VideoUploadEvent.Progress(percent)) },
        )
        val response = service.addVideo(
            video = MultipartBody.Part.createFormData(
                VIDEO_PART,
                "$UPLOAD_FILE_NAME.${video.format.extension}",
                body,
            ),
            title = MultipartBody.Part.createFormData(TITLE_PART, video.title),
            description = video.description?.let { MultipartBody.Part.createFormData(DESCRIPTION_PART, it) },
        )
        send(VideoUploadEvent.Completed(response.video.toEntity()))
    }.catch { error ->
        throw if (error is CancellationException || error is DomainException) {
            error
        } else {
            DomainException(error.toDomainError())
        }
    }

    override suspend fun deleteVideo(id: String): Result<Unit> {
        return runCatching { service.deleteVideo(id) }.toDomainResult()
    }
}
