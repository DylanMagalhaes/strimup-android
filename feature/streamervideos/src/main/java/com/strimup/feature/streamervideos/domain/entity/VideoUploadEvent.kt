package com.strimup.feature.streamervideos.domain.entity

import com.strimup.core.streamer.domain.entity.Streamer

sealed interface VideoUploadEvent {
    data class Progress(val percent: Int) : VideoUploadEvent

    data class Completed(val video: Streamer.Video) : VideoUploadEvent
}
