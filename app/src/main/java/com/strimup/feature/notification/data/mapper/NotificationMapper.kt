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

private const val TYPE_NEW_FAVORITE = "new_favorite"
private const val TYPE_GLOBAL_ANNOUNCEMENT = "global_announcement"
private const val TYPE_UGC_MESSAGE = "ugc_message"
private const val TYPE_UGC_ORDER_STATUS = "ugc_order_status"
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
        type = toNotificationType(type = type, data = data as? JsonObject),
        message = message,
        createdAt = createdAt?.toInstantOrNull(),
        isRead = readAt != null,
    )
}

private fun toNotificationType(type: String, data: JsonObject?): NotificationType = when (type) {
    TYPE_NEW_FAVORITE -> NotificationType.NewFavorite(
        fanId = data?.stringOrNull(DATA_FAN_ID),
        fanPseudo = data?.stringOrNull(DATA_FAN_PSEUDO),
    )
    TYPE_GLOBAL_ANNOUNCEMENT -> NotificationType.GlobalAnnouncement
    TYPE_UGC_MESSAGE -> NotificationType.UgcMessage
    TYPE_UGC_ORDER_STATUS -> NotificationType.UgcOrderStatus
    else -> NotificationType.Unknown(rawType = type)
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
