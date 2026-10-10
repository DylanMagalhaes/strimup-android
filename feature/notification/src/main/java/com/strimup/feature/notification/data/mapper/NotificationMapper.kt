package com.strimup.feature.notification.data.mapper

import com.strimup.feature.notification.data.response.NotificationPageResponse
import com.strimup.feature.notification.data.response.NotificationResponse
import com.strimup.feature.notification.domain.entity.Notification
import com.strimup.feature.notification.domain.entity.NotificationPage
import com.strimup.feature.notification.domain.entity.NotificationType
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.time.Instant
import java.time.format.DateTimeParseException

private const val DATA_FAN_ID = "fan_id"
private const val DATA_FAN_PSEUDO = "fan_pseudo"

fun NotificationPageResponse.toDomain(): NotificationPage {
    return NotificationPage(
        items = items.map { it.toDomain() },
        total = total,
        hasMore = hasMore,
    )
}

fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = id,
        type = (data as? JsonObject).let { payload ->
            NotificationType.fromApi(
                type = type,
                fanId = payload?.stringOrNull(DATA_FAN_ID),
                fanPseudo = payload?.stringOrNull(DATA_FAN_PSEUDO),
            )
        },
        message = message,
        createdAt = createdAt?.toInstantOrNull(),
        isRead = readAt != null,
    )
}

private fun JsonObject.stringOrNull(key: String): String? {
    val value: JsonElement = get(key) ?: return null
    return (value as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }
}

private fun String.toInstantOrNull(): Instant? = try {
    Instant.parse(this)
} catch (_: DateTimeParseException) {
    null
}
