package com.strimup.feature.notification.presentation.bell

private const val MAX_DISPLAYED_UNREAD_COUNT = 9

fun unreadBadgeLabel(unreadCount: Int): String? = when {
    unreadCount <= 0 -> null
    unreadCount > MAX_DISPLAYED_UNREAD_COUNT -> "$MAX_DISPLAYED_UNREAD_COUNT+"
    else -> unreadCount.toString()
}
