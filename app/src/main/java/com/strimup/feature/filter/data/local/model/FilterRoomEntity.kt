package com.strimup.feature.filter.data.local.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class TagRoomModel(
    val id: Int,
    val name: String,
    val category: String
)

@Entity(tableName = "filters")
data class FilterRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val userId: String,
    val minAge: Int,
    val maxAge: Int,
    val languages: List<String>,
    val platforms: List<String>,
    val personalities: List<String>,
    val tags: List<TagRoomModel>,
    val averageViewers: String,
    val streamFrequency: String,
    val status: String
)