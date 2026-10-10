package com.strimup.feature.push.presentation

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.strimup.core.ui.R
import com.strimup.feature.push.domain.entity.PushChannel
import com.strimup.feature.push.domain.entity.PushMessage
import com.strimup.feature.push.presentation.settings.canPostNotifications
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PushNotificationPublisher @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun publish(message: PushMessage) {
        if (!context.canPostNotifications()) return

        val channel = PushChannel.from(message.type)
        val title = message.title ?: context.getString(message.type.defaultPushTitleRes())
        val notification = NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.brand_pink))
            .setContentTitle(title)
            .setContentText(message.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
            .setAutoCancel(true)
            .setContentIntent(openNotificationsPendingIntent())
            .build()

        try {
            NotificationManagerCompat.from(context).notify(message.notificationKey(), notification)
        } catch (_: SecurityException) {
            return
        }
    }

    private fun openNotificationsPendingIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        OPEN_NOTIFICATIONS_REQUEST_CODE,
        PushIntents.openNotifications(context),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun PushMessage.notificationKey(): Int = notificationId?.hashCode() ?: body.hashCode()

    private companion object {
        const val OPEN_NOTIFICATIONS_REQUEST_CODE = 0
    }
}
