package com.strimup.feature.profile.data.mapper

import com.strimup.feature.profile.data.response.StreamerResponse
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity

fun StreamerResponse.toEntity(): ProfileStreamerEntity {

    val profile = this.streamerProfile
    val socialsList = listOf(
        ProfileStreamerEntity.Social(
            url = profile?.twitchUrl, type = ProfileStreamerEntity.Social.Type.Twitch
        ),
        ProfileStreamerEntity.Social(
            url = profile?.youtubeUrl, type = ProfileStreamerEntity.Social.Type.Youtube,
        ),
        ProfileStreamerEntity.Social(
            url = profile?.instagramUrl, type = ProfileStreamerEntity.Social.Type.Instagram,
        ),
        ProfileStreamerEntity.Social(
            url = profile?.tiktokUrl, type = ProfileStreamerEntity.Social.Type.Tiktok,
        ),
        ProfileStreamerEntity.Social(
            url = profile?.kickUrl, type = ProfileStreamerEntity.Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }


    return ProfileStreamerEntity(
        userName = requireNotNull(this.pseudo),
        imageUrl = this.streamerProfile?.avatarUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),
        bio = this.streamerProfile.bio ?: "",
        dailyStatus = this.streamerProfile.dailyStatus ?: "",
        socials = socialsList,
        tags = this.streamerProfile?.tags?.map {
            ProfileStreamerEntity.Tag(
                name = requireNotNull(it.name),
                category = requireNotNull(it.category)
            )
        },
        videos = this.streamerProfile?.videos?.map {
            ProfileStreamerEntity.Video(
                title = requireNotNull(it.title),
                description = requireNotNull(it.description),
                url = requireNotNull(it.url),
                order = requireNotNull(it.order),
            )
        }
    )
}