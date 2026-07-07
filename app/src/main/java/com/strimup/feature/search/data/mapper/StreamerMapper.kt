package com.strimup.feature.search.data.mapper

import com.strimup.feature.search.data.response.StreamersResponse
import com.strimup.feature.search.domain.entity.StreamerEntity

fun StreamersResponse.toEntity(): StreamerEntity {
    val sp = this.streamerProfile
    return StreamerEntity(
        id = sp.id,
        userName = this.userName,
        imageUrl = sp.imageUrl
    )
}