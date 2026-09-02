package com.strimup.core.favorite.data.mapper

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.favorite.data.local.model.FavoriteRoomEntity
import com.strimup.core.favorite.data.response.FavoriteStreamerResponse

fun FavoriteStreamerResponse.toDomain(): Streamer {
    return Streamer(
        id = this.id,
        userName = this.pseudo,
        imageUrl = this.avatarUrl,
        isLive = this.isLive,
        liveTitle = this.liveTitle
    )
}

fun FavoriteStreamerResponse.toRoomEntity(): FavoriteRoomEntity {
    return FavoriteRoomEntity(
        id = this.id,
        userName = this.pseudo,
        avatarUrl = this.avatarUrl
    )
}

fun FavoriteRoomEntity.toDomain(): Streamer {
    return Streamer(
        id = this.id,
        userName = this.userName,
        imageUrl = this.avatarUrl
    )
}