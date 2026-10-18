package com.strimup.feature.filter.domain.entity

data class FilterOptionsEntity(
    val averageViewers: List<String>,
    val languages: List<String>,
    val personalities: List<String>,
    val streamFrequencies: List<String>,
    val platforms: List<String> = listOf("Twitch","Youtube","Instagram","Kick","Tiktok")
)