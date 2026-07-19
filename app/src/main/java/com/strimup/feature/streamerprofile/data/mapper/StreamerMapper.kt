package com.strimup.feature.streamerprofile.data.mapper

import com.strimup.feature.streamerprofile.data.request.UpdateProfileRequest
import com.strimup.feature.streamerprofile.data.response.StreamerOptionsResponse
import com.strimup.feature.streamerprofile.data.response.StreamerResponse
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
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
                id = it.id ?: 0,
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

fun StreamerProfileEntity.toRequest(): UpdateProfileRequest {
    val twitchUrl = socials.firstOrNull { it.type == StreamerProfileEntity.Social.Type.Twitch }?.url
    val youtubeUrl = socials.firstOrNull { it.type == StreamerProfileEntity.Social.Type.Youtube }?.url
    val instagramUrl = socials.firstOrNull { it.type == StreamerProfileEntity.Social.Type.Instagram }?.url
    val tiktokUrl = socials.firstOrNull { it.type == StreamerProfileEntity.Social.Type.Tiktok }?.url
    val kickUrl = socials.firstOrNull { it.type == StreamerProfileEntity.Social.Type.Kick }?.url

    return UpdateProfileRequest(
        bio = bio?.takeIf { it.isNotBlank() },
        dailyStatus = dailyStatus?.takeIf { it.isNotBlank() },
        twitchUrl = twitchUrl?.takeIf { it.isNotBlank() },
        youtubeUrl = youtubeUrl?.takeIf { it.isNotBlank() },
        instagramUrl = instagramUrl?.takeIf { it.isNotBlank() },
        tiktokUrl = tiktokUrl?.takeIf { it.isNotBlank() },
        kickUrl = kickUrl?.takeIf { it.isNotBlank() },
        personality = personality,
        personalitySecondary = personalitySecondary,
        streamFrequency = streamFrequency,
        averageViewers = averageViewers,
        languages = languages?.filter { it.isNotBlank() },
        tags = tags?.map { it.id } ?: emptyList(),
        videos = emptyList()
    )
}

fun StreamerOptionsResponse.toEntity(): StreamerOptionsEntity{
    return StreamerOptionsEntity(
        averageViewers = this.averageViewers,
        languages = this.languages,
        personalities = this.personalities,
        streamFrequencies = this.streamFrequencies,
    )
}