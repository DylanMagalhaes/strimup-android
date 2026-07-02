package com.strimup.feature.home.data.mapper

import com.strimup.feature.home.data.response.InLiveStreamersResponse
import com.strimup.feature.home.data.response.RandomStreamersResponse
import com.strimup.feature.home.data.response.StreamersFoundResponse
import com.strimup.feature.home.domain.entity.StreamerEntity
import com.strimup.feature.home.domain.entity.StreamerEntity.Social

fun RandomStreamersResponse.StreamerData.toEntity(isFavorite: Boolean): StreamerEntity {
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

fun StreamersFoundResponse.toEntity(): StreamerEntity {
    val tagsList = this.streamerProfile.tags?.map { tag ->
        StreamerEntity.Tags(
            id = tag.id,
            name = tag.name
        )
    } ?: emptyList()

    return StreamerEntity(
        id = requireNotNull(this.id),
        userName = requireNotNull(this.userName),
        socials = emptyList(),
        imageUrl = this.streamerProfile.imageUrl ?: "",
        isLive = false,
        liveTitle = null,
        isFavorite = false,
        tags = tagsList
    )
}
