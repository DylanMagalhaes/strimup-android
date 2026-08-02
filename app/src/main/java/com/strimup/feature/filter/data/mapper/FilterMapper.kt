package com.strimup.feature.filter.data.mapper

import com.strimup.feature.filter.data.response.FilterJsonDto
import com.strimup.feature.filter.data.response.FilterResponse
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity

fun FilterResponse.toDomain(): FilterEntity {
    return FilterEntity(
        id = id,
        name = name,
        criteria = filterJson.toDomain(),
        userId = userId
    )
}

fun FilterJsonDto.toDomain(): FilterCriteria {
    val age = if (ageRange != null && ageRange.size >= 2) {
        ageRange[0]..ageRange[1]
    } else {
        18..80
    }

    return FilterCriteria(
        ageRange = age,
        category = category.orEmpty(),
        languages = languages ?: emptyList(),
        platforms = platforms ?: emptyList(),
        personalities = personalities ?: emptyList(),
        subCategories = subCategories ?: emptyList(),
        averageViewers = averageViewers.orEmpty(),
        streamFrequency = streamFrequency.orEmpty()
    )
}

fun FilterCriteria.toDto(): FilterJsonDto {
    return FilterJsonDto(
        ageRange = ageRange?.let { listOf(it.first, it.last) },
        category = category.ifBlank { null },
        languages = languages.ifEmpty { null },
        platforms = platforms.ifEmpty { null },
        personalities = personalities.ifEmpty { null },
        subCategories = subCategories.ifEmpty { null },
        averageViewers = averageViewers.ifBlank { null },
        streamFrequency = streamFrequency.ifBlank { null }
    )
}

