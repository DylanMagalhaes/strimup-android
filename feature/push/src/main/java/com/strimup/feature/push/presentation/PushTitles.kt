package com.strimup.feature.push.presentation

import androidx.annotation.StringRes
import com.strimup.feature.notification.domain.entity.NotificationType
import com.strimup.feature.push.R
import com.strimup.feature.push.domain.entity.PushChannel

@StringRes
fun NotificationType.defaultPushTitleRes(): Int = when (this) {
    is NotificationType.NewFavorite -> R.string.push_title_new_favorite
    else -> R.string.push_title_default
}

@StringRes
fun PushChannel.nameRes(): Int = when (this) {
    PushChannel.FAVORITES -> R.string.push_channel_favorites_name
    PushChannel.ANNOUNCEMENTS -> R.string.push_channel_announcements_name
    PushChannel.GENERAL -> R.string.push_channel_general_name
}

@StringRes
fun PushChannel.descriptionRes(): Int = when (this) {
    PushChannel.FAVORITES -> R.string.push_channel_favorites_description
    PushChannel.ANNOUNCEMENTS -> R.string.push_channel_announcements_description
    PushChannel.GENERAL -> R.string.push_channel_general_description
}
