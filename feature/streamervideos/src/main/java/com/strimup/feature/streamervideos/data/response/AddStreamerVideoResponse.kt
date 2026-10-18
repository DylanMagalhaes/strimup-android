package com.strimup.feature.streamervideos.data.response

import com.strimup.core.streamer.data.response.StreamerDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddStreamerVideoResponse(
    @SerialName("video")
    val video: StreamerDto.Video,
)
