package com.strimup.core.streamer.data.mapper

import com.strimup.core.streamer.data.request.UpdateProfileRequest
import com.strimup.core.streamer.data.response.FilterOptionsResponse
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.data.response.StreamerListResponse
import com.strimup.core.streamer.data.response.UpdateAvatarResponse
import com.strimup.core.streamer.data.response.UpdateProfileResponse
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.tag.domain.entity.TagEntity
import java.time.Instant
import java.time.format.DateTimeParseException

fun StreamerDto.toEntity(isFavorite: Boolean? = null): Streamer {
    return Streamer(
        id = this.id,
        userName = this.username,
        imageUrl = this.avatarUrl,
        socials = this.platforms.mapNotNull { it.toEntity() },
        isLive = this.isLive,
        liveTitle = this.liveTitle,
        isFavorite = isFavorite,
        bio = this.bio,
        dailyStatus = this.dailyStatus,
        followersCount = this.followersCount,
        tags = this.tags.map {
            TagEntity(id = it.id, name = it.name, category = it.category)
        },
        videos = this.videos.map { it.toEntity() },
        averageViewers = this.averageViewers,
        languages = this.languages,
        personality = this.personality,
        personalitySecondary = this.personalitySecondary,
        streamFrequency = this.streamFrequency,
        nextLive = this.nextLive?.toEntity(),
    )
}

fun StreamerDto.NextLive.toEntity(): Streamer.NextLive? {
    val instant = try {
        Instant.parse(startsAt)
    } catch (_: DateTimeParseException) {
        return null
    }
    return Streamer.NextLive(title = title, startsAt = instant)
}

fun StreamerDto.Social.toEntity(): Social? {
    val socialType = this.type.toSocialType() ?: return null
    return Social(url = this.url, type = socialType)
}

private fun String.toSocialType(): Social.Type? =
    when (this.lowercase()) {
        "twitch" -> Social.Type.Twitch
        "youtube", "yt" -> Social.Type.Youtube
        "instagram", "insta" -> Social.Type.Instagram
        "tiktok" -> Social.Type.Tiktok
        "kick" -> Social.Type.Kick
        else -> null
    }


fun StreamerListResponse.toDomain(): StreamerMatchResult {
    return StreamerMatchResult(
        total = total,
        streamers = items.map { it.toEntity() }
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
        tags = tags?.map { it.id } ?: emptyList()
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
