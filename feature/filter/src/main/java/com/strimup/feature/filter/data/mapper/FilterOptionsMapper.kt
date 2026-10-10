package com.strimup.feature.filter.data.mapper

import com.strimup.core.streamer.data.response.FilterOptionsResponse
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity


fun FilterOptionsResponse.toEntity(): FilterOptionsEntity {
    return FilterOptionsEntity(
        averageViewers = this.averageViewers,
        languages = this.languages,
        personalities = this.personalities,
        streamFrequencies = this.streamFrequencies,
    )
}