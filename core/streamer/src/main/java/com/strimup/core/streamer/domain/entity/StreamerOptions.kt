package com.strimup.core.streamer.domain.entity

data class StreamerOptions(
    val averageViewers: List<String>,
    val languages: List<String>,
    val personalities: List<String>,
    val streamFrequencies: List<String>
)
