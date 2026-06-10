package com.strimup.feature.home.data.mapper

import com.strimup.feature.home.data.response.InLiveStreamersResponse
import com.strimup.feature.home.data.response.RandomStreamersResponse
import com.strimup.feature.home.domain.entity.StreamerEntity
import com.strimup.feature.home.domain.entity.StreamerEntity.Social

fun RandomStreamersResponse.StreamerData.toEntity(): StreamerEntity {
    val profile = this.streamerProfile
    val socialsList = listOf(
        Social(
            url = profile?.twitchUrl, type = Social.Type.Twitch
        ),
        StreamerEntity.Social(
            url = profile?.youtubeUrl, type = Social.Type.Youtube,
        ),
        StreamerEntity.Social(
            url = profile?.instagramUrl, type = Social.Type.Instagram,
        ),
        StreamerEntity.Social(
            url = profile?.tiktokUrl, type = Social.Type.Tiktok,
        ),
        StreamerEntity.Social(
            url = profile?.kickUrl, type = Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }

    return StreamerEntity(
        userName = requireNotNull(this.pseudo),
        socials = socialsList,
        imageUrl = this.streamerProfile?.avatarUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),
    )
}

fun InLiveStreamersResponse.StreamerData.toEntity(): StreamerEntity {
    val profile = this.streamerProfile
    val socialsList = listOf(
        Social(
            url = profile?.twitchUrl, type = Social.Type.Twitch
        ),
        StreamerEntity.Social(
            url = profile?.youtubeUrl, type = Social.Type.Youtube,
        ),
        StreamerEntity.Social(
            url = profile?.instagramUrl, type = Social.Type.Instagram,
        ),
        StreamerEntity.Social(
            url = profile?.tiktokUrl, type = Social.Type.Tiktok,
        ),
        StreamerEntity.Social(
            url = profile?.kickUrl, type = Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }

    return StreamerEntity(
        userName = requireNotNull(this.pseudo),
        socials = socialsList,
        imageUrl = this.streamerProfile?.avatarUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),

    )
}