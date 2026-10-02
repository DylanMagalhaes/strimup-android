package com.strimup.feature.home.data.mapper

import com.strimup.feature.home.data.local.model.HomeBannerRoomEntity
import com.strimup.feature.home.data.response.BannerItemsResponse
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.BannerType

fun BannerItemsResponse.toRoomEntity(orderIndex: Int): HomeBannerRoomEntity {
    return HomeBannerRoomEntity(
        orderIndex = orderIndex,
        title = title,
        description = description,
        imageUrl = imageUrl.orEmpty(),
        position = position,
        linkUrl = linkUrl,
        type = type,
        avatarUrl = streamer?.avatarUrl,
        streamerId = streamer?.id,
    )
}

fun HomeBannerRoomEntity.toDomain(): BannerItemEntity {
    return BannerItemEntity(
        title = title,
        description = description,
        imageUrl = imageUrl,
        position = position,
        linkUrl = linkUrl,
        type = BannerType.fromApi(type),
        avatarUrl = avatarUrl,
        streamerId = streamerId,
    )
}
