package com.strimup.feature.notification.domain.entity

data class NotificationPage(
    val items: List<Notification>,
    val total: Int,
    val hasMore: Boolean,
)
