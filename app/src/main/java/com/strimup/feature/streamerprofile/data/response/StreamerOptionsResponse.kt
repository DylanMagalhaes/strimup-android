package com.strimup.feature.streamerprofile.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamerOptionsResponse(
    @SerialName("average_viewers")
    val averageViewers: List<String>,
    @SerialName("languages")
    val languages: List<String>,
    @SerialName("personalities")
    val personalities: List<String>,
    @SerialName("stream_frequencies")
    val streamFrequencies: List<String>,

    )
