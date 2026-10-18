package com.strimup.feature.home.domain.entity

enum class BannerType(private val apiValue: String?) {
    FeaturedStreamer("FEATURED_STREAMER"),
    Other(null);

    companion object {
        fun fromApi(value: String): BannerType = entries.firstOrNull { it.apiValue == value } ?: Other
    }
}
