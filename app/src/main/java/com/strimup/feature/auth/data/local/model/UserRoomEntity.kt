package com.strimup.feature.auth.data.local.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
data class UserRoomEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val userName: String,
    val role: String,
)
