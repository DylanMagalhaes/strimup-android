package com.strimup.feature.cuts.fake

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.cuts.domain.entity.Cut

fun cut(id: String, likesCount: Int = 0, isLikedByMe: Boolean = false) = Cut(
    video = Streamer.Video(
        id = id,
        title = "Vidéo $id",
        description = "",
        url = "https://cdn.strimup.com/profile-videos/$id.mp4",
        order = 1,
    ),
    author = Cut.Author(id = "user-$id", username = "Streamer $id", avatarUrl = "", isVerified = false, isLive = false),
    likesCount = likesCount,
    isLikedByMe = isLikedByMe,
)

fun cuts(vararg ids: String) = ids.map { cut(it) }
