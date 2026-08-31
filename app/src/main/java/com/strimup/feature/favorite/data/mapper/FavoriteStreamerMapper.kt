package com.strimup.feature.favorite.data.mapper

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.favorite.data.response.FavoriteStreamerResponse

fun FavoriteStreamerResponse.toDomain(): Streamer{
    return Streamer(
        id = this.id,
        userName = this.pseudo,
        imageUrl = this.avatarUrl,
        isLive = this.isLive,
        liveTitle = this.liveTitle
    )
}