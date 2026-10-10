package com.strimup.feature.streamervideos.domain.entity

enum class VideoFormat(val mimeType: String, val extension: String) {
    MP4(mimeType = "video/mp4", extension = "mp4"),
    MOV(mimeType = "video/quicktime", extension = "mov"),
    WEBM(mimeType = "video/webm", extension = "webm"),
}
