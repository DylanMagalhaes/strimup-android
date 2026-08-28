package com.strimup.core.streamer.data.mapper

import com.strimup.core.streamer.data.request.UpdateProfileRequest
import com.strimup.core.streamer.data.response.FilterOptionsResponse
import com.strimup.core.streamer.data.response.InLiveStreamersResponse
import com.strimup.core.streamer.data.response.MatchedStreamerDto
import com.strimup.core.streamer.data.response.RandomStreamersResponse
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.data.response.StreamerMatchResponse
import com.strimup.core.streamer.data.response.StreamersResponse
import com.strimup.core.streamer.data.response.UpdateAvatarResponse
import com.strimup.core.streamer.data.response.UpdateProfileResponse
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.tag.domain.entity.TagEntity

fun StreamerDto.toEntity(): Streamer {
    val profile = this.streamerProfile
    val socialsList = listOfNotNull(
        profile?.twitchUrl?.let { Social(url = it, type = Social.Type.Twitch) },
        profile?.youtubeUrl?.let { Social(url = it, type = Social.Type.Youtube) },
        profile?.instagramUrl?.let { Social(url = it, type = Social.Type.Instagram) },
        profile?.tiktokUrl?.let { Social(url = it, type = Social.Type.Tiktok) },
        profile?.kickUrl?.let { Social(url = it, type = Social.Type.Kick) },
    )

    return Streamer(
        id = this.id ?: "",
        userName = this.pseudo ?: "",
        imageUrl = profile?.avatarUrl,
        socials = socialsList,
        isLive = profile?.isLive ?: false,
        liveTitle = profile?.liveTitle,
        isFavorite = null,
        bio = profile?.bio,
        dailyStatus = profile?.dailyStatus,
        followersCount = profile?.followersCount,
        tags = profile?.tags?.map {
            TagEntity(id = it.id ?: 0, name = it.name ?: "", category = it.category ?: "")
        },
        videos = profile?.videos.orEmpty().map {
            Streamer.Video(
                id = it.id ?: "",
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
        streamFrequency = profile?.streamFrequency,
    )
}

fun RandomStreamersResponse.StreamerData.toEntity(isFavorite: Boolean): Streamer {
    val profile = this.streamerProfile
    val socialsList = listOfNotNull(
        profile?.twitchUrl?.let { Social(url = it, type = Social.Type.Twitch) },
        profile?.youtubeUrl?.let { Social(url = it, type = Social.Type.Youtube) },
        profile?.instagramUrl?.let { Social(url = it, type = Social.Type.Instagram) },
        profile?.tiktokUrl?.let { Social(url = it, type = Social.Type.Tiktok) },
        profile?.kickUrl?.let { Social(url = it, type = Social.Type.Kick) },
    )

    return Streamer(
        id = requireNotNull(this.id),
        userName = requireNotNull(this.userName),
        imageUrl = profile?.imageUrl ?: "",
        socials = socialsList,
        isLive = requireNotNull(profile?.isLive),
        liveTitle = profile?.liveTitle,
        isFavorite = isFavorite,
        bio = null,
        dailyStatus = profile?.dailyStatus,
        followersCount = null,
        tags = null,
        videos = emptyList(),
        averageViewers = null,
        languages = null,
        personality = null,
        personalitySecondary = null,
        streamFrequency = null,
    )
}

fun InLiveStreamersResponse.StreamerData.toEntity(isFavorite: Boolean): Streamer {
    val profile = this.streamerProfile
    val socialsList = listOfNotNull(
        profile?.twitchUrl?.let { Social(url = it, type = Social.Type.Twitch) },
        profile?.youtubeUrl?.let { Social(url = it, type = Social.Type.Youtube) },
        profile?.instagramUrl?.let { Social(url = it, type = Social.Type.Instagram) },
        profile?.tiktokUrl?.let { Social(url = it, type = Social.Type.Tiktok) },
        profile?.kickUrl?.let { Social(url = it, type = Social.Type.Kick) },
    )

    return Streamer(
        id = requireNotNull(this.id),
        userName = requireNotNull(this.userName),
        imageUrl = profile?.imageUrl ?: "",
        socials = socialsList,
        isLive = requireNotNull(profile?.isLive),
        liveTitle = profile?.liveTitle,
        isFavorite = isFavorite,
        bio = null,
        dailyStatus = null,
        followersCount = null,
        tags = null,
        videos = emptyList(),
        averageViewers = null,
        languages = null,
        personality = null,
        personalitySecondary = null,
        streamFrequency = null,
    )
}

fun StreamersResponse.toEntity(): Streamer {
    return Streamer(
        id = this.id,
        userName = this.userName,
        imageUrl = this.streamerProfile.imageUrl,
        socials = emptyList(),
        isLive = false,
        liveTitle = null,
        isFavorite = null,
        bio = null,
        dailyStatus = null,
        followersCount = null,
        tags = null,
        videos = emptyList(),
        averageViewers = null,
        languages = null,
        personality = null,
        personalitySecondary = null,
        streamFrequency = null,
    )
}

fun MatchedStreamerDto.toDomain(): Streamer {
    val socialsList = listOfNotNull(
        twitchUrl?.let { Social(url = it, type = Social.Type.Twitch) },
        youtubeUrl?.let { Social(url = it, type = Social.Type.Youtube) },
        instagramUrl?.let { Social(url = it, type = Social.Type.Instagram) },
        tiktokUrl?.let { Social(url = it, type = Social.Type.Tiktok) },
        kickUrl?.let { Social(url = it, type = Social.Type.Kick) },
    )

    return Streamer(
        id = id,
        userName = username,
        imageUrl = imageUrl,
        socials = socialsList,
        isLive = isLive,
        liveTitle = liveTitle,
        isFavorite = false,
        bio = null,
        dailyStatus = null,
        followersCount = null,
        tags = null,
        videos = emptyList(),
        averageViewers = null,
        languages = null,
        personality = null,
        personalitySecondary = null,
        streamFrequency = null,
    )
}

fun StreamerMatchResponse.toDomain(): StreamerMatchResult {
    return StreamerMatchResult(
        total = total,
        streamers = matchedStreamers.map { it.toDomain() }
    )
}

fun UpdateProfileResponse.Streamer.toEntity(): Streamer {
    val socialsList = listOfNotNull(
        twitchUrl?.let { Social(url = it, type = Social.Type.Twitch) },
        youtubeUrl?.let { Social(url = it, type = Social.Type.Youtube) },
        instagramUrl?.let { Social(url = it, type = Social.Type.Instagram) },
        tiktokUrl?.let { Social(url = it, type = Social.Type.Tiktok) },
        kickUrl?.let { Social(url = it, type = Social.Type.Kick) },
    )

    return Streamer(
        id = id,
        userName = "",
        imageUrl = avatarUrl,
        socials = socialsList,
        isLive = isLive,
        liveTitle = liveTitle,
        isFavorite = null,
        bio = bio,
        dailyStatus = dailyStatus,
        followersCount = null,
        tags = tags.map { TagEntity(id = it.id, name = it.name, category = it.category) },
        videos = emptyList(),
        averageViewers = averageViewers,
        languages = languages,
        personality = personality,
        personalitySecondary = personalitySecondary,
        streamFrequency = streamFrequency,
    )
}

fun Streamer.toUpdateProfileRequest(): UpdateProfileRequest {
    val twitchUrl = socials.firstOrNull { it.type == Social.Type.Twitch }?.url
    val youtubeUrl = socials.firstOrNull { it.type == Social.Type.Youtube }?.url
    val instagramUrl = socials.firstOrNull { it.type == Social.Type.Instagram }?.url
    val tiktokUrl = socials.firstOrNull { it.type == Social.Type.Tiktok }?.url
    val kickUrl = socials.firstOrNull { it.type == Social.Type.Kick }?.url

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

fun FilterOptionsResponse.toEntity(): StreamerOptions {
    return StreamerOptions(
        averageViewers = this.averageViewers,
        languages = this.languages,
        personalities = this.personalities,
        streamFrequencies = this.streamFrequencies,
    )
}

fun UpdateAvatarResponse.toDomain(): String = avatarUrl
