package com.strimup.feature.filter.data.mapper

import com.strimup.core.streamer.data.request.FilterJsonDto
import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.data.request.TagDto
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.feature.filter.data.local.model.FilterRoomEntity
import com.strimup.feature.filter.data.local.model.TagRoomModel
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

fun FilterCriteria.toStreamerMatchRequest(page: Int): StreamerMatchRequest {
    return StreamerMatchRequest(
        filter = toDto(),
        page = page,
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


fun TagEntity.toRoomModel(): TagRoomModel = TagRoomModel(
    id = id,
    name = name,
    category = category
)

fun TagRoomModel.toDomain(): TagEntity = TagEntity(
    id = id,
    name = name,
    category = category
)

fun FilterEntity.toRoomEntity(): FilterRoomEntity {
    return FilterRoomEntity(
        id = id,
        name = name,
        userId = userId,
        minAge = criteria.ageRange.first,
        maxAge = criteria.ageRange.last,
        languages = criteria.languages,
        platforms = criteria.platforms,
        personalities = criteria.personalities,
        tags = criteria.tags.map { it.toRoomModel() },
        averageViewers = criteria.averageViewers,
        streamFrequency = criteria.streamFrequency,
        status = criteria.status,
    )
}

fun FilterRoomEntity.toDomainEntity(): FilterEntity {
    return FilterEntity(
        id = id,
        name = name,
        userId = userId,
        criteria = FilterCriteria(
            ageRange = minAge..maxAge,
            languages = languages,
            platforms = platforms,
            personalities = personalities,
            tags = tags.map { it.toDomain() },
            averageViewers = averageViewers,
            streamFrequency = streamFrequency,
            status = status
        )
    )
}
