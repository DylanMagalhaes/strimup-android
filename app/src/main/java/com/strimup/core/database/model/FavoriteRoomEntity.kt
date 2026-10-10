package com.strimup.core.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteRoomEntity(
    @PrimaryKey
    val id: String,
    val userName: String,
    val imageUrl: String?
)
