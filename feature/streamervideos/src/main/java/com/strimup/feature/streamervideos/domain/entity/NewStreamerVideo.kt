package com.strimup.feature.streamervideos.domain.entity

data class NewStreamerVideo(
    val file: LocalVideoFile,
    val format: VideoFormat,
    val title: String,
    val description: String?,
)
