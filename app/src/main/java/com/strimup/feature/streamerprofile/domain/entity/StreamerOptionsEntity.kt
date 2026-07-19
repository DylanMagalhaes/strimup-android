package com.strimup.feature.streamerprofile.domain.entity

data class StreamerOptionsEntity(
    val averageViewers: List<String>,
    val languages: List<String>,
    val personalities: List<String>,
    val streamFrequencies: List<String>
)
