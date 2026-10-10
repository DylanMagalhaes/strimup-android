package com.strimup.feature.home.data.mapper

import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.database.model.HomeSocialRoomModel
import com.strimup.core.database.model.HomeStreamerRoomEntity
import com.strimup.core.database.model.HomeTagRoomModel

fun Streamer.toHomeRoomEntity(orderIndex: Int): HomeStreamerRoomEntity {
    return HomeStreamerRoomEntity(
        id = id,
        orderIndex = orderIndex,
        userName = userName,
        imageUrl = imageUrl,
        socials = socials.map { social -> HomeSocialRoomModel(url = social.url, type = social.type.name) },
        tags = tags.orEmpty().map { tag -> HomeTagRoomModel(id = tag.id, name = tag.name, category = tag.category) },
        personality = personality,
        personalitySecondary = personalitySecondary,
    )
}

fun HomeStreamerRoomEntity.toStreamer(): Streamer {
    return Streamer(
        id = id,
        userName = userName,
        imageUrl = imageUrl,
        socials = socials.mapNotNull { social -> social.toSocial() },
        isLive = false,
        liveTitle = null,
        tags = tags.map { tag -> TagEntity(id = tag.id, name = tag.name, category = tag.category) },
        personality = personality,
        personalitySecondary = personalitySecondary,
    )
}

private fun HomeSocialRoomModel.toSocial(): Social? =
    Social.Type.entries.firstOrNull { it.name == type }?.let { socialType -> Social(url = url, type = socialType) }
