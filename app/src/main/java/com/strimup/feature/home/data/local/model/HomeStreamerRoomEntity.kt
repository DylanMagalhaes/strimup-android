package com.strimup.feature.home.data.local.model

import androidx.room3.ColumnTypeConverters
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "home_streamers")
@ColumnTypeConverters(HomeConverters::class)
data class HomeStreamerRoomEntity(
    @PrimaryKey
    val id: String,
    val orderIndex: Int,
    val userName: String,
    val imageUrl: String?,
    val socials: List<HomeSocialRoomModel>,
    val tags: List<HomeTagRoomModel>,
    val personality: String?,
    val personalitySecondary: String?,
)
