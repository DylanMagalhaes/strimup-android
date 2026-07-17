package com.strimup.feature.streamerprofile.data.mapper

import com.strimup.feature.streamerprofile.data.response.StreamerResponse
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

fun StreamerResponse.toEntity(): StreamerProfileEntity {
    val profile = this.streamerProfile

    val socialsList = listOf(
        StreamerProfileEntity.Social(
            url = profile?.twitchUrl, type = StreamerProfileEntity.Social.Type.Twitch
        ),
        StreamerProfileEntity.Social(
            url = profile?.youtubeUrl, type = StreamerProfileEntity.Social.Type.Youtube
        ),
        StreamerProfileEntity.Social(
            url = profile?.instagramUrl, type = StreamerProfileEntity.Social.Type.Instagram
        ),
        StreamerProfileEntity.Social(
            url = profile?.tiktokUrl, type = StreamerProfileEntity.Social.Type.Tiktok
        ),
        StreamerProfileEntity.Social(
            url = profile?.kickUrl, type = StreamerProfileEntity.Social.Type.Kick
        )
    ).filter { !it.url.isNullOrBlank() }

    return StreamerProfileEntity(
        userName = this.pseudo ?: "Inconnu",
        imageUrl = profile?.avatarUrl ?: "",
        isLive = profile?.isLive ?: false,
        bio = profile?.bio,
        dailyStatus = profile?.dailyStatus,
        socials = socialsList,
        tags = profile?.tags?.map {
            StreamerProfileEntity.Tag(
                name = it.name ?: "",
                category = it.category ?: ""
            )
        },
        videos = profile?.videos?.map {
            StreamerProfileEntity.Video(
                title = it.title ?: "",
                description = it.description ?: "",
                url = it.url ?: "",
                order = it.order ?: 0
            )
        },
        averageViewers = profile?.averageViewers,
        languages = profile?.languages,
        personality = profile?.personality,
        personalitySecondary = profile?.personalitySecondary,
        streamFrequency = profile?.streamFrequency
    )
}