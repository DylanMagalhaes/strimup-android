package com.strimup.feature.home.domain.entity

data class BannerItemEntity(
    val title: String,
    val description:String,
    val imageUrl: String,
    val position: Int,
    val linkUrl: String,
    val type: String,
    val avatarUrl: String?,
    val streamerId: String ?,
)

