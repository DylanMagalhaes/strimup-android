package com.strimup.feature.filter.data.mapper

import com.strimup.common.domain.entity.TagEntity
import com.strimup.feature.filter.data.response.FilterJsonDto
import com.strimup.feature.filter.data.response.FilterResponse
import com.strimup.feature.filter.data.response.TagDto
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
        languages = languages ?: emptyList(),
        platforms = platforms ?: emptyList(),
        personalities = personalities ?: emptyList(),
        tags = tags?.map { it.toDomain() } ?: emptyList(),
        averageViewers = averageViewers.orEmpty(),
        streamFrequency = streamFrequency.orEmpty(),
        status = status.orEmpty()
    )
}

fun FilterCriteria.toDto(): FilterJsonDto {
    return FilterJsonDto(
        ageRange = listOf(ageRange.first, ageRange.last),
        languages = languages.ifEmpty { null },
        platforms = platforms.ifEmpty { null },
        personalities = personalities.ifEmpty { null },
        tags = tags.map { it.toDto() }.ifEmpty { null },
        averageViewers = averageViewers.ifBlank { null },
        streamFrequency = streamFrequency.ifBlank { null },
        status = status.ifBlank { null }
    )
}

fun TagDto.toDomain(): TagEntity = TagEntity(
    id = id,
    name = name,
    category = category.orEmpty()
)

fun TagEntity.toDto(): TagDto = TagDto(
    id = id,
    name = name,
    category = category.ifBlank { null }
)