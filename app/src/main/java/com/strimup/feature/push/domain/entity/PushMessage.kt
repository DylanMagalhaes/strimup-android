package com.strimup.feature.push.domain.entity

import com.strimup.feature.notification.domain.entity.NotificationType

data class PushMessage(
    val notificationId: String?,
    val type: NotificationType,
    val title: String?,
    val body: String,
)
