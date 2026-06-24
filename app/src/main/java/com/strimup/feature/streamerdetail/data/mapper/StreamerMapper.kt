package com.strimup.feature.streamerdetail.data.mapper

import com.strimup.feature.streamerdetail.data.response.StreamerResponse
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity

fun StreamerResponse.toEntity(): StreamerDetailEntity {

    val profile = this.streamerProfile
    val socialsList = listOf(
        StreamerDetailEntity.Social(
            url = profile?.twitchUrl, type = StreamerDetailEntity.Social.Type.Twitch
        ),
        StreamerDetailEntity.Social(
            url = profile?.youtubeUrl, type = StreamerDetailEntity.Social.Type.Youtube,
        ),
        StreamerDetailEntity.Social(
            url = profile?.instagramUrl, type = StreamerDetailEntity.Social.Type.Instagram,
        ),
        StreamerDetailEntity.Social(
            url = profile?.tiktokUrl, type = StreamerDetailEntity.Social.Type.Tiktok,
        ),
        StreamerDetailEntity.Social(
            url = profile?.kickUrl, type = StreamerDetailEntity.Social.Type.Kick,
        )
    ).filter {
        it.url != null
    }


    return StreamerDetailEntity(
        userName = requireNotNull(this.pseudo),
        imageUrl = this.streamerProfile?.avatarUrl ?: "",
        isLive = requireNotNull(this.streamerProfile?.isLive),
        bio = this.streamerProfile.bio ?: "",
        dailyStatus = this.streamerProfile.dailyStatus ?: "",
        socials = socialsList,
        tags = this.streamerProfile?.tags?.map {
            StreamerDetailEntity.Tag(
                name = requireNotNull(it.name),
                category = requireNotNull(it.category)
            )
        },
        videos = this.streamerProfile?.videos?.map {
            StreamerDetailEntity.Video(
                title = requireNotNull(it.title),
                description = requireNotNull(it.description),
                url = requireNotNull(it.url),
                order = requireNotNull(it.order),
            )
        }
    )
}