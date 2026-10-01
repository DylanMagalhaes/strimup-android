package com.strimup.feature.push.presentation

import android.content.Context
import android.content.Intent
import com.strimup.MainActivity

object PushIntents {
    private const val ACTION_OPEN_NOTIFICATIONS = "com.strimup.action.OPEN_NOTIFICATIONS"

    fun openNotifications(context: Context): Intent =
        Intent(context, MainActivity::class.java)
            .setAction(ACTION_OPEN_NOTIFICATIONS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

    fun isOpenNotifications(intent: Intent?): Boolean = intent?.action == ACTION_OPEN_NOTIFICATIONS
}
