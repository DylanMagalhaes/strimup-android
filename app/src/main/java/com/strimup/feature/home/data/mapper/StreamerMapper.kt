package com.strimup.feature.home.data.mapper

import com.strimup.feature.home.data.response.RandomStreamersResponse
import com.strimup.feature.home.domain.entity.StreamerEntity

fun RandomStreamersResponse.StreamerData.toEntity(): StreamerEntity =
    StreamerEntity(
        userName = requireNotNull(this.pseudo),
        socials = emptyList(),
        imageUrl = this.streamerProfile?.avatarUrl ?: "",
    )