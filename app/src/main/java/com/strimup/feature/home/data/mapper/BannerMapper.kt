package com.strimup.feature.home.data.mapper

import com.strimup.feature.home.data.response.BannerItemsResponse
import com.strimup.feature.home.domain.entity.BannerItemEntity

fun BannerItemsResponse.toDomain(): BannerItemEntity{
    return BannerItemEntity(
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        linkUrl = this.linkUrl,
        position = this.position
    )
}