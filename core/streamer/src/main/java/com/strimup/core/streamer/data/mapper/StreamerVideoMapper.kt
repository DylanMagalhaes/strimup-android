package com.strimup.core.streamer.data.mapper

import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.domain.entity.Streamer

fun StreamerDto.Video.toEntity(): Streamer.Video {
    return Streamer.Video(
        id = id,
        title = title,
        description = description.orEmpty(),
        url = url,
        order = order,
    )
}
