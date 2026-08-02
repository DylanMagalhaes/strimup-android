package com.strimup.feature.filter.domain.entity

data class FilterEntity(
    val id: String,
    val name: String,
    val criteria: FilterCriteria,
    val userId: String
)

data class FilterCriteria(
    val ageRange: IntRange = 18..80,
    val category: String = "",
    val languages: List<String> = emptyList(),
    val platforms: List<String> = emptyList(),
    val personalities: List<String> = emptyList(),
    val subCategories: List<String> = emptyList(),
    val averageViewers: String = "",
    val streamFrequency: String = ""
)
