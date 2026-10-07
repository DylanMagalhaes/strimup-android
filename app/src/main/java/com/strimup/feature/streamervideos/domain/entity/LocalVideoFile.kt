package com.strimup.feature.streamervideos.domain.entity

data class LocalVideoFile(
    val uri: String,
    val fileName: String?,
    val mimeType: String?,
    val sizeBytes: Long?,
)
