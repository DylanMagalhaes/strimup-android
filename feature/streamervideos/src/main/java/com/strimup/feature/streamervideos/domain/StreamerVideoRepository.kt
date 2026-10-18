package com.strimup.feature.streamervideos.domain

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideo
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.flow.Flow

interface StreamerVideoRepository {
    suspend fun getMyVideos(): Result<List<Streamer.Video>>

    suspend fun getLocalVideoFile(uri: String): Result<LocalVideoFile>

    fun uploadVideo(video: NewStreamerVideo): Flow<VideoUploadEvent>

    suspend fun deleteVideo(id: String): Result<Unit>
}
