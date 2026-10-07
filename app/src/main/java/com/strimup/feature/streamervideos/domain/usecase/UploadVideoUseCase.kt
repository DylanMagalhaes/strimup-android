package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.flow.Flow

fun interface UploadVideoUseCase {
    operator fun invoke(file: LocalVideoFile, title: String, description: String): Flow<VideoUploadEvent>
}
