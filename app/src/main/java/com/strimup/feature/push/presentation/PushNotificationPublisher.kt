package com.strimup.feature.push.presentation

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.strimup.R
import com.strimup.feature.push.domain.entity.PushChannel
import com.strimup.feature.push.domain.entity.PushMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PushNotificationPublisher @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun publish(message: PushMessage) {
        if (!canPostNotifications()) return

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

    private fun canPostNotifications(): Boolean {
        val isPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        return isPermissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
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
