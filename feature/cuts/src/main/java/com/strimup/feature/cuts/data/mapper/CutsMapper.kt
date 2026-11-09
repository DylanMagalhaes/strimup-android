package com.strimup.feature.cuts.data.mapper

import com.strimup.core.streamer.data.mapper.toEntity
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.feature.cuts.data.response.CutLikeResponse
import com.strimup.feature.cuts.data.response.CutsResponse
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.CutLike
import com.strimup.feature.cuts.domain.entity.CutsPage

fun CutsResponse.toEntity(): CutsPage = CutsPage(
    cuts = items.map { it.toEntity() },
    nextCursor = nextCursor?.takeIf { it.isNotBlank() },
)

fun CutsResponse.Item.toEntity(): Cut = Cut(
    video = StreamerDto.Video(
        id = video.id,
        title = video.title,
        url = video.url,
        description = video.description,
        order = video.order,
        embed = video.embed,
        thumbnailUrl = video.thumbnailUrl,
    ).toEntity(),
    author = Cut.Author(
        id = streamer.id,
        username = streamer.username,
        avatarUrl = streamer.avatarUrl.orEmpty(),
        isVerified = streamer.isVerified,
        isLive = streamer.isLive,
    ),
    likesCount = video.likesCount,
    isLikedByMe = video.likedByMe,
)

fun CutLikeResponse.toEntity(): CutLike = CutLike(isLiked = liked, likesCount = likesCount)
