package com.strimup.feature.streamervideos.domain

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideo
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeStreamerVideoRepository(
    var myVideosResult: Result<List<Streamer.Video>> = Result.success(emptyList()),
    var localFileResult: Result<LocalVideoFile> = Result.failure(IllegalStateException()),
    var uploadEvents: Flow<VideoUploadEvent> = flowOf(),
    var deleteResult: Result<Unit> = Result.success(Unit),
) : StreamerVideoRepository {
    val uploadedVideos = mutableListOf<NewStreamerVideo>()
    val deletedIds = mutableListOf<String>()

    override suspend fun getMyVideos(): Result<List<Streamer.Video>> = myVideosResult

    override suspend fun getLocalVideoFile(uri: String): Result<LocalVideoFile> = localFileResult

    override fun uploadVideo(video: NewStreamerVideo): Flow<VideoUploadEvent> {
        uploadedVideos += video
        return uploadEvents
    }

    override suspend fun deleteVideo(id: String): Result<Unit> {
        deletedIds += id
        return deleteResult
    }
}
