package com.strimup.feature.push.data.mapper

import com.strimup.feature.notification.domain.entity.NotificationType
import com.strimup.feature.push.domain.entity.PushMessage

private const val KEY_NOTIFICATION_ID = "notification_id"
private const val KEY_TYPE = "type"
private const val KEY_TITLE = "title"
private const val KEY_BODY = "body"

fun Map<String, String>.toPushMessage(): PushMessage? {
    val body = valueOrNull(KEY_BODY) ?: return null

    return PushMessage(
        notificationId = valueOrNull(KEY_NOTIFICATION_ID),
        type = NotificationType.fromApi(type = valueOrNull(KEY_TYPE).orEmpty()),
        title = valueOrNull(KEY_TITLE),
        body = body,
    )
}

private fun Map<String, String>.valueOrNull(key: String): String? = get(key)?.takeIf { it.isNotBlank() }
