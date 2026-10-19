package com.strimup.feature.search.data.mapper

import com.strimup.core.database.model.RecentStreamerRoomEntity
import com.strimup.core.streamer.domain.entity.Streamer

fun Streamer.toRecentStreamerRoomEntity(viewedAt: Long): RecentStreamerRoomEntity {
    return RecentStreamerRoomEntity(
        id = id,
        userName = userName,
        imageUrl = imageUrl,
        viewedAt = viewedAt,
    )
}

fun RecentStreamerRoomEntity.toStreamer(): Streamer {
    return Streamer(
        id = id,
        userName = userName,
        imageUrl = imageUrl,
    )
}
