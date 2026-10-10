package com.strimup.feature.push.presentation

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.strimup.feature.push.domain.entity.PushChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PushNotificationChannels @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun create() {
        val channels = PushChannel.entries.map { channel ->
            NotificationChannelCompat.Builder(channel.id, channel.importance())
                .setName(context.getString(channel.nameRes()))
                .setDescription(context.getString(channel.descriptionRes()))
                .build()
        }
        NotificationManagerCompat.from(context).createNotificationChannelsCompat(channels)
    }

    private fun PushChannel.importance(): Int = when (this) {
        PushChannel.FAVORITES -> NotificationManagerCompat.IMPORTANCE_HIGH
        PushChannel.ANNOUNCEMENTS,
        PushChannel.GENERAL -> NotificationManagerCompat.IMPORTANCE_DEFAULT
    }
}
