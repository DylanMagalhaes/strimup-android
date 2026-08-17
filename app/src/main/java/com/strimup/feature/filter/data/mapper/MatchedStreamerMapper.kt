package com.strimup.feature.filter.data.mapper

import com.strimup.common.domain.entity.StreamerEntity
import com.strimup.common.domain.entity.StreamerEntity.Social
import com.strimup.feature.filter.data.response.MatchedStreamerDto

fun MatchedStreamerDto.toDomain(): StreamerEntity {
    val socialsList = listOf(
    Social(
        url = twitchUrl, type = Social.Type.Twitch
    ),
    Social(
        url = youtubeUrl, type = Social.Type.Youtube,
    ),
    Social(
        url = instagramUrl, type = Social.Type.Instagram,
    ),
    Social(
        url = tiktokUrl, type = Social.Type.Tiktok,
    ),
    Social(
        url = kickUrl, type = Social.Type.Kick,
    )
    ).filter {
        it.url != null
    }

    return StreamerEntity(
        id = id,
        userName = username,
        imageUrl = imageUrl,
        isLive = isLive,
        liveTitle = liveTitle,
        socials = socialsList,
        isFavorite = false,
        tags = null
    )
}