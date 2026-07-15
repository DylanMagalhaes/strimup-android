package com.strimup.feature.streamerprofile.data.mapper

import com.strimup.feature.streamerdetail.data.response.StreamerResponse
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

fun StreamerResponse.toEntity(): StreamerProfileEntity {

    val profile = this.streamerProfile
    val socialsList = listOf(
        StreamerProfileEntity.Social(
            url = profile?.twitchUrl, type = StreamerProfileEntity.Social.Type.Twitch
        ),
        StreamerProfileEntity.Social(
            url = profile?.youtubeUrl, type = StreamerProfileEntity.Social.Type.Youtube,
        ),
        StreamerProfileEntity.Social(
            url = profile?.instagramUrl, type = StreamerProfileEntity.Social.Type.Instagram,
        ),
        StreamerProfileEntity.Social(
            url = profile?.tiktokUrl, type = StreamerProfileEntity.Social.Type.Tiktok,
        ),
        StreamerProfileEntity.Social(
            url = profile?.kickUrl, type = StreamerProfileEntity.Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }


    return StreamerProfileEntity(
        userName = requireNotNull(this.pseudo),
        imageUrl = this.streamerProfile?.avatarUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),
        bio = this.streamerProfile.bio ?: "",
        dailyStatus = this.streamerProfile.dailyStatus ?: "",
        socials = socialsList,
        tags = this.streamerProfile?.tags?.map {
            StreamerProfileEntity.Tag(
                name = requireNotNull(it.name),
                category = requireNotNull(it.category)
            )
        },
        videos = this.streamerProfile?.videos?.map {
            StreamerProfileEntity.Video(
                title = requireNotNull(it.title),
                description = requireNotNull(it.description),
                url = requireNotNull(it.url),
                order = requireNotNull(it.order),
            )
        }
    )
}