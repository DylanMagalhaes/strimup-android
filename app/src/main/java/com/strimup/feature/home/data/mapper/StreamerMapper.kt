package com.strimup.feature.home.data.mapper

import com.strimup.feature.home.data.response.InLiveStreamersResponse
import com.strimup.feature.home.data.response.RandomStreamersResponse
import com.strimup.common.domain.entity.StreamerEntity
import com.strimup.common.domain.entity.StreamerEntity.Social

fun RandomStreamersResponse.StreamerData.toEntity(isFavorite: Boolean): StreamerEntity {
    val profile = this.streamerProfile
    val socialsList = listOf(
        Social(
            url = profile?.twitchUrl, type = Social.Type.Twitch
        ),
        Social(
            url = profile?.youtubeUrl, type = Social.Type.Youtube,
        ),
        Social(
            url = profile?.instagramUrl, type = Social.Type.Instagram,
        ),
        Social(
            url = profile?.tiktokUrl, type = Social.Type.Tiktok,
        ),
        Social(
            url = profile?.kickUrl, type = Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }


    return StreamerEntity(
        id = requireNotNull(this.id),
        userName = requireNotNull(this.userName),
        socials = socialsList,
        imageUrl = this.streamerProfile?.imageUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),
        liveTitle = this.streamerProfile.liveTitle,
        isFavorite = isFavorite,
        tags = null
    )
}

fun InLiveStreamersResponse.StreamerData.toEntity(isFavorite: Boolean): StreamerEntity {
    val profile = this.streamerProfile
    val socialsList = listOf(
        Social(
            url = profile?.twitchUrl, type = Social.Type.Twitch
        ),
        Social(
            url = profile?.youtubeUrl, type = Social.Type.Youtube,
        ),
        Social(
            url = profile?.instagramUrl, type = Social.Type.Instagram,
        ),
        Social(
            url = profile?.tiktokUrl, type = Social.Type.Tiktok,
        ),
        Social(
            url = profile?.kickUrl, type = Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }

    return StreamerEntity(
        id = requireNotNull(this.id),
        userName = requireNotNull(this.userName),
        socials = socialsList,
        imageUrl = this.streamerProfile?.imageUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),
        liveTitle = this.streamerProfile.liveTitle,
        isFavorite = isFavorite,
        tags = null
    )
}
