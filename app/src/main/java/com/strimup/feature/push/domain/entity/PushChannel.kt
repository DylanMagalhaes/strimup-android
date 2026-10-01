package com.strimup.feature.push.domain.entity

import com.strimup.feature.notification.domain.entity.NotificationType

enum class PushChannel(val id: String) {
    FAVORITES("favorites"),
    ANNOUNCEMENTS("announcements"),
    GENERAL("general");

    companion object {
        fun from(type: NotificationType): PushChannel = when (type) {
            is NotificationType.NewFavorite -> FAVORITES
            NotificationType.GlobalAnnouncement -> ANNOUNCEMENTS
            NotificationType.UgcMessage,
            NotificationType.UgcOrderStatus,
            is NotificationType.Unknown -> GENERAL
        }
    }
}
