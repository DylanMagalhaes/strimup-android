package com.strimup.feature.home.data.local.model

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json

class HomeConverters {

    @ColumnTypeConverter
    fun fromSocials(value: List<HomeSocialRoomModel>): String = json.encodeToString(value)

    @ColumnTypeConverter
    fun toSocials(value: String): List<HomeSocialRoomModel> = json.decodeFromString(value)

    @ColumnTypeConverter
    fun fromTags(value: List<HomeTagRoomModel>): String = json.encodeToString(value)

    @ColumnTypeConverter
    fun toTags(value: String): List<HomeTagRoomModel> = json.decodeFromString(value)

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
