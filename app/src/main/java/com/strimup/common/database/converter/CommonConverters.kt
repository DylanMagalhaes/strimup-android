package com.strimup.common.database.converter

import androidx.room3.ColumnTypeConverter
import com.strimup.feature.filter.data.local.model.TagRoomModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CommonConverters {

    @ColumnTypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @ColumnTypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)

    @ColumnTypeConverter
    fun fromTagList(value: List<TagRoomModel>): String = Json.encodeToString(value)

    @ColumnTypeConverter
    fun toTagList(value: String): List<TagRoomModel> = Json.decodeFromString(value)
}