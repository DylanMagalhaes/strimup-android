package com.strimup.core.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "home_banner")
data class HomeBannerRoomEntity(
    @PrimaryKey
    val orderIndex: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val position: Int,
    val linkUrl: String,
    val type: String,
    val avatarUrl: String?,
    val streamerId: String?,
)
