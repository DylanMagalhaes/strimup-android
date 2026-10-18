package com.strimup.feature.filter.domain.entity

import com.strimup.core.tag.domain.entity.TagEntity

data class FilterEntity(
    val id: String,
    val name: String,
    val criteria: FilterCriteria,
    val userId: String
)

data class FilterCriteria(
    val ageRange: IntRange = 18..80,
    val tags: List<TagEntity> = emptyList(),
    val languages: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val personalities: List<String> = emptyList(),
    val averageViewers: String = "",
    val streamFrequency: String = "",
    val status: String = ""
)
