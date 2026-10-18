package com.strimup.feature.notification.domain.entity

import java.time.Instant

data class Notification(
    val id: String,
    val type: NotificationType,
    val message: String,
    val createdAt: Instant?,
    val isRead: Boolean,
) {
    val isDeletable: Boolean
        get() = type !is NotificationType.GlobalAnnouncement
}
