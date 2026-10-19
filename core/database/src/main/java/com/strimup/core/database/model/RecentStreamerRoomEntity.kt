package com.strimup.core.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "recent_streamers")
data class RecentStreamerRoomEntity(
    @PrimaryKey
    val id: String,
    val userName: String,
    val imageUrl: String?,
    val viewedAt: Long,
)
